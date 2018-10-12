/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.api.AergoApi;
import hera.api.model.Account;
import hera.api.model.Authentication;
import hera.api.model.ContractAddress;
import hera.api.model.ContractCall;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.strategy.LocalSignStrategy;
import hera.strategy.NettyConnectStrategy;
import hera.strategy.RemoteSignStrategy;
import hera.util.IoUtils;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Before;
import org.junit.Test;

public class ContractTemplateIT extends AbstractIT {

  protected static final String PASSWORD = randomUUID().toString();

  protected AergoApi localSignAergoApi;

  protected AergoApi remoteSignAergoApi;

  protected String encodedPayload;

  @Before
  public void setUp() {
    super.setUp();
    localSignAergoApi = new AergoClientBuilder().addStrategy(new NettyConnectStrategy())
        .addStrategy(new LocalSignStrategy()).build();
    remoteSignAergoApi = new AergoClientBuilder().addStrategy(new NettyConnectStrategy())
        .addStrategy(new RemoteSignStrategy()).build();
    try {
      final Reader reader = new InputStreamReader(open("payload"));
      encodedPayload = IoUtils.from(reader);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Test
  public void testLuaContractDeployandExecuteBySigningLocally() throws Exception {
    final AergoKey key = new AergoKeyGenerator().create();
    final AtomicLong nonce = new AtomicLong(1);
    final ContractTxHash deployTxHash = localSignAergoApi.getContractOperation().deploy(key,
        key.getAddress(), nonce.getAndIncrement(), () -> encodedPayload);
    logger.debug("Deploy hash: {}", deployTxHash);

    waitForNextBlockToGenerate();

    final ContractTxReceipt definitionReceipt =
        localSignAergoApi.getContractOperation().getReceipt(deployTxHash);
    final ContractAddress contractAddress = definitionReceipt.getContractAddress();
    logger.debug("Deploy receipt: {}", definitionReceipt);
    assertTrue(!definitionReceipt.getContractAddress().getBytesValue().isEmpty());
    assertEquals("CREATED", definitionReceipt.getStatus());

    final ContractInterface contractInterface =
        localSignAergoApi.getContractOperation().getContractInterface(contractAddress);
    assertNotNull(contractInterface);
    logger.debug("Contract interface: {}", contractInterface);

    final ContractFunction executionFunction = contractInterface.findFunctionByName("exec").get();
    assertNotNull(executionFunction);
    logger.debug("Execution function: {}", executionFunction);

    final ContractCall executionCall = ContractCall.newBuilder().setAddress(contractAddress)
        .setFunction(executionFunction).setArgs("key1", "value1").build();
    final ContractTxHash executionTxHash = localSignAergoApi.getContractOperation().execute(key,
        key.getAddress(), nonce.getAndIncrement(), executionCall);
    assertNotNull(executionTxHash);
    logger.debug("Execution hash: {}", executionTxHash);

    waitForNextBlockToGenerate();

    final ContractTxReceipt executionReceipt =
        localSignAergoApi.getContractOperation().getReceipt(executionTxHash);
    logger.debug("Execution receipt: {}", definitionReceipt);
    assertTrue(!executionReceipt.getContractAddress().getBytesValue().isEmpty());
    assertEquals("SUCCESS", executionReceipt.getStatus());
    assertTrue(0 < executionReceipt.getRet().length());

    final ContractFunction queryFunction = contractInterface.findFunctionByName("query").get();
    assertNotNull(queryFunction);
    logger.debug("Query function: {}", queryFunction);

    final ContractCall queryCall =
        ContractCall.newBuilder().setAddress(contractAddress).setFunction(queryFunction).build();
    final ContractResult queryResult = localSignAergoApi.getContractOperation().query(queryCall);
    assertNotNull(queryResult);
    logger.debug("Query result: {}", queryResult);
  }

  @Test
  public void testLuaContractDeployandExecuteBySigningRemotely() throws Exception {
    final Account account = remoteSignAergoApi.getAccountOperation().create(PASSWORD);
    final AtomicLong nonce = new AtomicLong(1);

    boolean unlockResult = remoteSignAergoApi.getAccountOperation()
        .unlock(Authentication.of(account.getAddress(), PASSWORD));
    assertTrue(unlockResult);

    final ContractTxHash deployTxHash = remoteSignAergoApi.getContractOperation().deploy(null,
        account.getAddress(), nonce.getAndIncrement(), () -> encodedPayload);
    logger.debug("Deploy hash: {}", deployTxHash);

    waitForNextBlockToGenerate();

    final ContractTxReceipt definitionReceipt =
        remoteSignAergoApi.getContractOperation().getReceipt(deployTxHash);
    final ContractAddress contractAddress = definitionReceipt.getContractAddress();
    logger.debug("Deploy receipt: {}", definitionReceipt);
    assertTrue(!definitionReceipt.getContractAddress().getBytesValue().isEmpty());
    assertEquals("CREATED", definitionReceipt.getStatus());

    final ContractInterface contractInterface =
        remoteSignAergoApi.getContractOperation().getContractInterface(contractAddress);
    assertNotNull(contractInterface);
    logger.debug("Contract interface: {}", contractInterface);

    final ContractFunction executionFunction = contractInterface.findFunctionByName("exec").get();
    assertNotNull(executionFunction);
    logger.debug("Execution function: {}", executionFunction);

    final ContractCall executionCall = ContractCall.newBuilder().setAddress(contractAddress)
        .setFunction(executionFunction).setArgs("key1", "value1").build();
    final ContractTxHash executionTxHash = remoteSignAergoApi.getContractOperation().execute(null,
        account.getAddress(), nonce.getAndIncrement(), executionCall);
    assertNotNull(executionTxHash);
    logger.debug("Execution hash: {}", executionTxHash);

    waitForNextBlockToGenerate();

    final ContractTxReceipt executionReceipt =
        remoteSignAergoApi.getContractOperation().getReceipt(executionTxHash);
    logger.debug("Execution receipt: {}", definitionReceipt);
    assertTrue(!executionReceipt.getContractAddress().getBytesValue().isEmpty());
    assertEquals("SUCCESS", executionReceipt.getStatus());
    assertTrue(0 < executionReceipt.getRet().length());

    final ContractFunction queryFunction = contractInterface.findFunctionByName("query").get();
    assertNotNull(queryFunction);
    logger.debug("Query function: {}", queryFunction);

    final ContractCall queryCall =
        ContractCall.newBuilder().setAddress(contractAddress).setFunction(queryFunction).build();
    final ContractResult queryResult = remoteSignAergoApi.getContractOperation().query(queryCall);
    assertNotNull(queryResult);
    logger.debug("Query result: {}", queryResult);

    boolean lockResult = remoteSignAergoApi.getAccountOperation()
        .lock(Authentication.of(account.getAddress(), PASSWORD));
    assertTrue(lockResult);
  }
}
