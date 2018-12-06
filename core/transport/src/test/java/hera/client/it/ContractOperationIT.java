/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.math.BigInteger.valueOf;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.api.model.Account;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Fee;
import hera.util.IoUtils;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.Before;
import org.junit.Test;

public class ContractOperationIT extends AbstractIT {

  protected String contractPayload;

  @Before
  public void setUp() {
    try {
      contractPayload = IoUtils.from(new InputStreamReader(open("payload")));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  protected ContractTxHash define(final Account account, final Fee fee, final Object... args) {
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(contractPayload).constructorArgs(args).build();
    logger.info("Deploy definition: {}", definition);
    return aergoClient.getContractOperation().deploy(account, definition, fee);
  }

  protected ContractTxReceipt getContractTxReceipt(final ContractTxHash contractTxHash) {
    final ContractTxReceipt receipt = aergoClient.getContractOperation().getReceipt(contractTxHash);
    logger.info("Contract tx receipt: {}", receipt);
    return receipt;
  }

  protected ContractInterface getContractInterface(final ContractAddress contractAddress) {
    final ContractInterface contractInterface =
        aergoClient.getContractOperation().getContractInterface(contractAddress);
    logger.info("Contract interface: {}", contractInterface);
    return contractInterface;
  }

  protected ContractTxHash execute(final Account account, final Fee fee,
      final ContractInterface contractInterface, final String function, final Object... args) {
    final ContractInvocation execution =
        contractInterface.newInvocation().function(function).args(args).build();
    logger.info("Contract invocation: {}", execution);
    return aergoClient.getContractOperation().execute(account, execution, Fee.of(valueOf(0L), 0L));
  }

  protected ContractResult query(final ContractInterface contractInterface, final String function,
      final Object... args) {
    final ContractInvocation query =
        contractInterface.newInvocation().function(function).args(args).build();
    logger.info("Query invocation : {}", query);
    return aergoClient.getContractOperation().query(query);
  }

  @Test
  public void testLuaContractConstructor() throws Exception {
    final Account account = createClientAccount();
    rechargeCoin(account, 100L);

    waitForNextBlockToGenerate();

    final String key = "key";
    final int intVal = 100;
    final String stringVal = "string value";
    final ContractTxHash deployTxHash =
        define(account, Fee.of(valueOf(1L), 1L), key, intVal, stringVal);

    waitForNextBlockToGenerate();

    final ContractTxReceipt deployTxReceipt = getContractTxReceipt(deployTxHash);
    assertEquals("CREATED", deployTxReceipt.getStatus());

    final ContractInterface contractInterface =
        getContractInterface(deployTxReceipt.getContractAddress());

    final ContractResult queryResult = query(contractInterface, "get", key);
    final Data data = queryResult.bind(Data.class);
    assertEquals(intVal, data.getIntVal());
    assertEquals(stringVal, data.getStringVal());
  }

  @Test
  public void testLuaContractDeployAndExecuteWithLocalAccount() throws Exception {
    final Account account = createClientAccount();
    rechargeCoin(account, 100L);

    waitForNextBlockToGenerate();

    final ContractTxHash deployTxHash = define(account, Fee.of(valueOf(1L), 1L));
    account.incrementNonce();

    waitForNextBlockToGenerate();

    final ContractTxReceipt deployTxReceipt = getContractTxReceipt(deployTxHash);
    assertEquals("CREATED", deployTxReceipt.getStatus());

    final ContractInterface contractInterface =
        getContractInterface(deployTxReceipt.getContractAddress());

    final String key = "key";
    final int intVal = 100;
    final String stringVal = "string value";
    final ContractTxHash executionTxHash =
        execute(account, Fee.of(valueOf(1L), 1L), contractInterface, "set", key, intVal, stringVal);
    account.incrementNonce();

    waitForNextBlockToGenerate();

    final ContractTxReceipt executionReceipt = getContractTxReceipt(executionTxHash);
    assertEquals("SUCCESS", executionReceipt.getStatus());

    final ContractResult queryResult = query(contractInterface, "get", key);
    final Data data = queryResult.bind(Data.class);
    assertEquals(intVal, data.getIntVal());
    assertEquals(stringVal, data.getStringVal());
  }

  @Test
  public void testLuaContractDeployAndExecuteWithRemoteAccount() throws Exception {
    final String password = randomUUID().toString();
    final Account account = createServerAccount(password);
    rechargeCoin(account, 100L);

    waitForNextBlockToGenerate();

    assertTrue(unlockAccount(account, password));

    final ContractTxHash deployTxHash = define(account, Fee.of(valueOf(1L), 1L));
    account.incrementNonce();

    waitForNextBlockToGenerate();

    final ContractTxReceipt deployTxReceipt = getContractTxReceipt(deployTxHash);
    assertEquals("CREATED", deployTxReceipt.getStatus());

    final ContractInterface contractInterface =
        getContractInterface(deployTxReceipt.getContractAddress());

    final String key = "key";
    final int intVal = 100;
    final String stringVal = "string value";
    final ContractTxHash executionTxHash =
        execute(account, Fee.of(valueOf(1L), 1L), contractInterface, "set", key, intVal, stringVal);
    account.incrementNonce();

    waitForNextBlockToGenerate();

    final ContractTxReceipt executionReceipt = getContractTxReceipt(executionTxHash);
    assertEquals("SUCCESS", executionReceipt.getStatus());

    final ContractResult queryResult = query(contractInterface, "get", key);
    final Data data = queryResult.bind(Data.class);
    assertEquals(intVal, data.getIntVal());
    assertEquals(stringVal, data.getStringVal());

    assertTrue(lockAccount(account, password));
  }

  @ToString
  public static class Data {

    @Getter
    @Setter
    protected int intVal;

    @Getter
    @Setter
    protected String stringVal;
  }

}
