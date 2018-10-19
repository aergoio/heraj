/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.api.model.Account;
import hera.api.model.Authentication;
import hera.api.model.ClientManagedAccount;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
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
import hera.strategy.NettyConnectStrategy;
import hera.util.IoUtils;
import java.io.InputStreamReader;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.Before;
import org.junit.Test;

public class ContractOperationIT extends AbstractIT {

  protected String helloContract;
  protected String constructorContract;

  @Before
  public void setUp() {
    try {
      helloContract = IoUtils.from(new InputStreamReader(open("hello")));
      constructorContract = IoUtils.from(new InputStreamReader(open("constructor")));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Test
  public void testLuaContractLocally() throws Exception {
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = ClientManagedAccount.of(key);

    final ContractDefinition definition = new ContractDefinition(() -> helloContract);
    final ContractTxHash deployTxHash =
        aergoClient.getContractOperation().deploy(account, definition);
    account.incrementNonce();
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
    final ContractInvocation execution =
        new ContractInvocation(contractAddress, executionFunction, new Object[] {"key1", "value1"});
    logger.info("Execution invocation : {}", execution);
    final ContractTxHash executionTxHash =
        aergoClient.getContractOperation().execute(account, execution);
    account.incrementNonce();
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
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    final String password = randomUUID().toString();

    final Account account = aergoClient.getAccountOperation().create(password);

    final boolean unlockResult =
        aergoClient.getAccountOperation().unlock(Authentication.of(account.getAddress(), password));
    assertTrue(unlockResult);

    final ContractDefinition definition = new ContractDefinition(() -> helloContract);
    final ContractTxHash deployTxHash =
        aergoClient.getContractOperation().deploy(account, definition);
    account.incrementNonce();
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
    final ContractInvocation execution =
        new ContractInvocation(contractAddress, executionFunction, new Object[] {"key1", "value1"});
    logger.info("Execution invocation : {}", execution);
    final ContractTxHash executionTxHash =
        aergoClient.getContractOperation().execute(account, execution);
    account.incrementNonce();
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

  @Test
  public void testLuaContractConstructor() throws Exception {
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = ClientManagedAccount.of(key);

    final ContractDefinition definition =
        new ContractDefinition(() -> constructorContract, new Object[] {1, "2"});
    final ContractTxHash deployTxHash =
        aergoClient.getContractOperation().deploy(account, definition);
    account.incrementNonce();
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

    final ContractFunction queryFunction = contractInterface.findFunction("get").get();
    final ContractInvocation query = new ContractInvocation(contractAddress, queryFunction);
    logger.info("Query invocation : {}", query);
    final ContractResult queryResult = aergoClient.getContractOperation().query(query);
    logger.info("Query result: {}", queryResult);
    final Data value = queryResult.bind(Data.class);
    logger.info("Binded query result: {}", value);

    aergoClient.close();
  }

  @ToString
  protected static class Data {
    @Getter
    @Setter
    protected int a;

    @Getter
    @Setter
    protected String b;
  }

}
