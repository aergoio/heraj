/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

  protected String deployKey = randomUUID().toString();
  protected int deployIntVal = randomUUID().toString().hashCode();
  protected String deployStringVal = randomUUID().toString();

  protected String executeFunction = "set";
  protected String executeKey = randomUUID().toString();
  protected int executeIntVal = randomUUID().toString().hashCode();
  protected String executeStringVal = randomUUID().toString();

  protected String queryFunction = "get";

  @Before
  public void setUp() {
    try {
      contractPayload = IoUtils.from(new InputStreamReader(open("payload")));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  protected ContractTxReceipt getContractTxReceipt(final ContractTxHash contractTxHash) {
    final ContractTxReceipt receipt = aergoClient.getContractOperation().getReceipt(contractTxHash);
    return receipt;
  }

  protected ContractInterface getContractInterface(final ContractAddress contractAddress) {
    final ContractInterface contractInterface =
        aergoClient.getContractOperation().getContractInterface(contractAddress);
    return contractInterface;
  }

  protected ContractResult query(final ContractInterface contractInterface, final String function,
      final Object... args) {
    final ContractInvocation query =
        contractInterface.newInvocationBuilder().function(function).args(args).build();
    return aergoClient.getContractOperation().query(query);
  }

  @Test
  public void testLuaContractConstructor() throws Exception {
    for (final Account account : supplyAccounts()) {
      final ContractDefinition definition = ContractDefinition.newBuilder()
          .encodedContract(contractPayload)
          .constructorArgs(deployKey, deployIntVal, deployStringVal)
          .build();
      unlockAccount(account, password);
      final ContractTxHash deployTxHash =
          aergoClient.getContractOperation().deploy(account, definition,
              account.incrementAndGetNonce(), fee);
      lockAccount(account, password);

      waitForNextBlockToGenerate();

      final ContractTxReceipt deployTxReceipt = getContractTxReceipt(deployTxHash);
      assertEquals("CREATED", deployTxReceipt.getStatus());

      final ContractInterface contractInterface =
          getContractInterface(deployTxReceipt.getContractAddress());

      final ContractResult queryResult = query(contractInterface, queryFunction, deployKey);
      final Data data = queryResult.bind(Data.class);
      assertEquals(deployIntVal, data.getIntVal());
      assertEquals(deployStringVal, data.getStringVal());
    }
  }

  @Test
  public void testLuaContractDeployAndExecute() throws Exception {
    for (final Account account : supplyAccounts()) {
      unlockAccount(account, password);
      final ContractDefinition definition = ContractDefinition.newBuilder()
          .encodedContract(contractPayload).build();
      final ContractTxHash deployTxHash = aergoClient.getContractOperation().deploy(account,
          definition, account.incrementAndGetNonce(), fee);
      lockAccount(account, password);

      waitForNextBlockToGenerate();

      final ContractTxReceipt deployTxReceipt = getContractTxReceipt(deployTxHash);
      assertEquals("CREATED", deployTxReceipt.getStatus());

      final ContractInterface contractInterface =
          getContractInterface(deployTxReceipt.getContractAddress());

      unlockAccount(account, password);
      final ContractInvocation execution = contractInterface.newInvocationBuilder()
          .function(executeFunction)
          .args(new Object[] {executeKey, executeIntVal, executeStringVal})
          .build();
      final ContractTxHash executionTxHash = aergoClient.getContractOperation().execute(account,
          execution, account.incrementAndGetNonce(), Fee.getDefaultFee());
      lockAccount(account, password);

      waitForNextBlockToGenerate();

      final ContractTxReceipt executionReceipt = getContractTxReceipt(executionTxHash);
      assertEquals("SUCCESS", executionReceipt.getStatus());

      final ContractResult queryResult = query(contractInterface, queryFunction, executeKey);
      final Data data = queryResult.bind(Data.class);
      assertEquals(executeIntVal, data.getIntVal());
      assertEquals(executeStringVal, data.getStringVal());
    }
  }

  @Test
  public void testLuaContractDeployWithInvalidNonce() throws Exception {
    for (final Account account : supplyAccounts()) {
      unlockAccount(account, password);
      final ContractDefinition definition = ContractDefinition.newBuilder()
          .encodedContract(contractPayload).build();
      try {
        aergoClient.getContractOperation().deploy(account,
            definition, account.getNonce(), fee);
        fail();
      } catch (Exception e) {
        // good we expected this
      }
      lockAccount(account, password);
    }
  }

  @Test
  public void testLuaContractExecuteWithInvalidNonce() throws Exception {
    for (final Account account : supplyAccounts()) {
      unlockAccount(account, password);
      final ContractDefinition definition = ContractDefinition.newBuilder()
          .encodedContract(contractPayload).build();
      final ContractTxHash deployTxHash = aergoClient.getContractOperation().deploy(account,
          definition, account.incrementAndGetNonce(), fee);
      lockAccount(account, password);

      waitForNextBlockToGenerate();

      final ContractTxReceipt deployTxReceipt = getContractTxReceipt(deployTxHash);
      assertEquals("CREATED", deployTxReceipt.getStatus());

      final ContractInterface contractInterface =
          getContractInterface(deployTxReceipt.getContractAddress());

      unlockAccount(account, password);
      final ContractInvocation execution = contractInterface.newInvocationBuilder()
          .function(executeFunction)
          .args(new Object[] {executeKey, executeIntVal, executeStringVal})
          .build();
      try {
        aergoClient.getContractOperation().execute(account,
            execution, account.getNonce(), Fee.getDefaultFee());
        fail();
      } catch (Exception e) {
        // good we expected this
      }
      lockAccount(account, password);
    }
  }

  @Test
  public void testLuaContractDeployOnLockedAccount() throws Exception {
    final Account account = aergoClient.getKeyStoreOperation().create(password);
    // unlockAccount(account, password);
    try {
      final ContractDefinition definition = ContractDefinition.newBuilder()
          .encodedContract(contractPayload).build();
      aergoClient.getContractOperation().deploy(account, definition,
          account.incrementAndGetNonce(), fee);
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testLuaContractExecuteOnLockedAccount() {
    final Account account = aergoClient.getKeyStoreOperation().create(password);
    unlockAccount(account, password);
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(contractPayload).build();
    final ContractTxHash deployTxHash =
        aergoClient.getContractOperation().deploy(account, definition,
            account.incrementAndGetNonce(), fee);
    lockAccount(account, password);

    waitForNextBlockToGenerate();

    final ContractTxReceipt deployTxReceipt = getContractTxReceipt(deployTxHash);
    assertEquals("CREATED", deployTxReceipt.getStatus());

    final ContractInterface contractInterface =
        getContractInterface(deployTxReceipt.getContractAddress());

    // unlockAccount(account, password);
    try {
      final ContractInvocation execution = contractInterface.newInvocationBuilder()
          .function(executeFunction)
          .args(executeKey, executeIntVal, executeStringVal)
          .build();
      aergoClient.getContractOperation().execute(account, execution, account.incrementAndGetNonce(),
          Fee.getDefaultFee());
    } catch (Exception e) {
      // good we expected this
    }
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
