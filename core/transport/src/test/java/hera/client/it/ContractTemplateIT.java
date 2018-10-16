/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.api.model.Account;
import hera.api.model.Authentication;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.strategy.LocalSignStrategy;
import hera.strategy.NettyConnectStrategy;
import hera.strategy.RemoteSignStrategy;
import hera.util.IoUtils;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Before;
import org.junit.Test;

public class ContractTemplateIT extends AbstractIT {


  protected String encodedPayload;

  @Before
  public void setUp() {
    try {
      final Reader reader = new InputStreamReader(open("payload"));
      encodedPayload = IoUtils.from(reader);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Test
  public void testLuaContractLocally() throws Exception {
    final AergoClient aergoClient = new AergoClientBuilder()
        .addStrategy(new NettyConnectStrategy(hostname))
        .addStrategy(new LocalSignStrategy())
        .build();

    final AergoKey key = new AergoKeyGenerator().create();
    final AtomicLong nonce = new AtomicLong(1);

    final ContractTxHash deployTxHash = aergoClient.getContractOperation().deploy(key,
        key.getAddress(), nonce.getAndIncrement(), () -> encodedPayload);
    logger.info("Deploy hash: {}", deployTxHash);

    waitForNextBlockToGenerate();

    final ContractTxReceipt definitionReceipt =
        aergoClient.getContractOperation().getReceipt(deployTxHash);
    logger.info("Deploy receipt: {}", definitionReceipt);
    assertEquals("CREATED", definitionReceipt.getStatus());

    final ContractAddress contractAddress = definitionReceipt.getContractAddress();

    final ContractInterface contractInterface =
        aergoClient.getContractOperation().getContractInterface(contractAddress);
    logger.info("Contract interface: {}", contractInterface);

    final ContractFunction executionFunction = contractInterface.findFunction("exec").get();
    final ContractInvocation execution = new ContractInvocation(contractAddress, executionFunction,
        Arrays.asList(new Object[] {"key1", "value1"}));
    logger.info("Execution invocation : {}", execution);
    final ContractTxHash executionTxHash = aergoClient.getContractOperation().execute(key,
        key.getAddress(), nonce.getAndIncrement(), execution);
    logger.info("Execution hash: {}", executionTxHash);

    waitForNextBlockToGenerate();

    final ContractTxReceipt executionReceipt =
        aergoClient.getContractOperation().getReceipt(executionTxHash);
    logger.info("Execution receipt: {}", executionReceipt);
    assertEquals("SUCCESS", executionReceipt.getStatus());
    assertTrue(0 < executionReceipt.getRet().length());

    final ContractFunction queryFunction = contractInterface.findFunction("query").get();
    final ContractInvocation query = new ContractInvocation(contractAddress, queryFunction);
    logger.info("Query invocation : {}", query);
    final ContractResult queryResult = aergoClient.getContractOperation().query(query);
    logger.info("Query result: {}", queryResult);

    aergoClient.close();
  }

  @Test
  public void testLuaContractRemotely() throws Exception {
    final AergoClient aergoClient = new AergoClientBuilder()
        .addStrategy(new NettyConnectStrategy(hostname))
        .addStrategy(new RemoteSignStrategy())
        .build();

    final String password = randomUUID().toString();

    final Account account = aergoClient.getAccountOperation().create(password);
    final AtomicLong nonce = new AtomicLong(1);

    final boolean unlockResult =
        aergoClient.getAccountOperation().unlock(Authentication.of(account.getAddress(), password));
    assertTrue(unlockResult);

    final ContractTxHash deployTxHash = aergoClient.getContractOperation().deploy(null,
        account.getAddress(), nonce.getAndIncrement(), () -> encodedPayload);
    logger.info("Deploy hash: {}", deployTxHash);

    waitForNextBlockToGenerate();

    final ContractTxReceipt definitionReceipt =
        aergoClient.getContractOperation().getReceipt(deployTxHash);
    logger.info("Deploy receipt: {}", definitionReceipt);
    assertEquals("CREATED", definitionReceipt.getStatus());

    final ContractAddress contractAddress = definitionReceipt.getContractAddress();

    final ContractInterface contractInterface =
        aergoClient.getContractOperation().getContractInterface(contractAddress);
    logger.info("Contract interface: {}", contractInterface);

    final ContractFunction executionFunction = contractInterface.findFunction("exec").get();
    final ContractInvocation execution = new ContractInvocation(contractAddress, executionFunction,
        Arrays.asList(new Object[] {"key1", "value1"}));
    logger.info("Execution invocation : {}", execution);
    final ContractTxHash executionTxHash = aergoClient.getContractOperation().execute(null,
        account.getAddress(), nonce.getAndIncrement(), execution);
    logger.info("Execution hash: {}", executionTxHash);

    waitForNextBlockToGenerate();

    final ContractTxReceipt executionReceipt =
        aergoClient.getContractOperation().getReceipt(executionTxHash);
    logger.info("Execution receipt: {}", executionReceipt);
    assertEquals("SUCCESS", executionReceipt.getStatus());
    assertTrue(0 < executionReceipt.getRet().length());

    final ContractFunction queryFunction = contractInterface.findFunction("query").get();
    final ContractInvocation query = new ContractInvocation(contractAddress, queryFunction);
    logger.info("Query invocation : {}", query);
    final ContractResult queryResult = aergoClient.getContractOperation().query(query);
    logger.info("Query result: {}", queryResult);

    final boolean lockResult =
        aergoClient.getAccountOperation().lock(Authentication.of(account.getAddress(), password));
    assertTrue(lockResult);

    aergoClient.close();
  }
}
