/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.Account;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.util.IoUtils;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
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
  public void setUp() throws Exception {
    super.setUp();
    contractPayload = IoUtils.from(new InputStreamReader(open("payload")));
  }

  protected ContractTxHash deploy(final Account account, final ContractDefinition definition) {
    final ContractTxHash txHash = aergoClient.getContractOperation().deploy(account,
        definition, account.incrementAndGetNonce());
    waitForNextBlockToGenerate();
    return txHash;
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

  protected ContractTxHash execute(final Account account, final ContractInvocation execution) {
    final ContractTxHash txHash = aergoClient.getContractOperation().execute(account,
        execution, account.incrementAndGetNonce());
    waitForNextBlockToGenerate();
    return txHash;
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
      final ContractTxHash deployTxHash = deploy(account, definition);
      lockAccount(account, password);

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
      final ContractTxHash deployTxHash = deploy(account, definition);
      lockAccount(account, password);

      final ContractTxReceipt deployTxReceipt = getContractTxReceipt(deployTxHash);
      assertEquals("CREATED", deployTxReceipt.getStatus());

      final ContractInterface contractInterface =
          getContractInterface(deployTxReceipt.getContractAddress());

      unlockAccount(account, password);
      final ContractInvocation execution = contractInterface.newInvocationBuilder()
          .function(executeFunction)
          .args(new Object[] {executeKey, executeIntVal, executeStringVal})
          .build();
      final ContractTxHash executionTxHash = execute(account, execution);
      lockAccount(account, password);

      final ContractTxReceipt executionReceipt = getContractTxReceipt(executionTxHash);
      assertEquals("SUCCESS", executionReceipt.getStatus());

      final ContractResult queryResult = query(contractInterface, queryFunction, executeKey);
      final Data data = queryResult.bind(Data.class);
      assertEquals(executeIntVal, data.getIntVal());
      assertEquals(executeStringVal, data.getStringVal());
    }
  }

  @Test
  public void testLuaContractDeployAndExecuteWithEscapeString() throws Exception {
    for (final Account account : supplyAccounts()) {
      unlockAccount(account, password);
      final ContractDefinition definition = ContractDefinition.newBuilder()
          .encodedContract(contractPayload).build();
      final ContractTxHash deployTxHash = deploy(account, definition);
      lockAccount(account, password);

      final ContractTxReceipt deployTxReceipt = getContractTxReceipt(deployTxHash);
      assertEquals("CREATED", deployTxReceipt.getStatus());

      final ContractInterface contractInterface =
          getContractInterface(deployTxReceipt.getContractAddress());

      unlockAccount(account, password);
      final String escapeString = "\b\t\f\n\r";
      final ContractInvocation execution = contractInterface.newInvocationBuilder()
          .function(executeFunction)
          .args(new Object[] {executeKey, executeIntVal, escapeString})
          .build();
      final ContractTxHash executionTxHash = execute(account, execution);
      lockAccount(account, password);

      final ContractTxReceipt executionReceipt = getContractTxReceipt(executionTxHash);
      assertEquals("SUCCESS", executionReceipt.getStatus());

      final ContractResult queryResult = query(contractInterface, queryFunction, executeKey);
      final Data data = queryResult.bind(Data.class);
      assertEquals(executeIntVal, data.getIntVal());
      assertEquals(escapeString, data.getStringVal());
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
            definition, account.getRecentlyUsedNonce());
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
      final ContractTxHash deployTxHash = deploy(account, definition);
      lockAccount(account, password);

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
        account.setNonce(0L);
        execute(account, execution);
        fail();
      } catch (Exception e) {
        // good we expected this
      }
      lockAccount(account, password);
    }
  }

  @Test
  public void testLuaContractDeployOnLockedAccount() throws Exception {
    final Account account = supplyServerAccount();
    // unlockAccount(account, password);
    try {
      final ContractDefinition definition = ContractDefinition.newBuilder()
          .encodedContract(contractPayload).build();
      deploy(account, definition);
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testLuaContractExecuteOnLockedAccount() {
    final Account account = supplyServerAccount();
    unlockAccount(account, password);
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(contractPayload).build();
    final ContractTxHash deployTxHash = deploy(account, definition);
    lockAccount(account, password);

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
      execute(account, execution);
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testLuaContractEvent() {
    for (final Account account : supplyAccounts()) {
      unlockAccount(account, password);
      final ContractDefinition definition = ContractDefinition.newBuilder()
          .encodedContract(contractPayload)
          .build();
      final ContractTxHash deployTxHash = deploy(account, definition);

      final ContractTxReceipt deployTxReceipt = getContractTxReceipt(deployTxHash);
      assertEquals("CREATED", deployTxReceipt.getStatus());

      final ContractAddress contractAddress = deployTxReceipt.getContractAddress();
      final ContractInterface contractInterface = getContractInterface(contractAddress);

      final EventFilter eventFilter = EventFilter.newBuilder(contractAddress)
          .recentBlockCount(100)
          .build();

      final CountDownLatch latch = new CountDownLatch(2);
      final Subscription<Event> subscription =
          aergoClient.getContractOperation().subscribeEvent(eventFilter,
              new StreamObserver<Event>() {

                @Override
                public void onNext(Event value) {
                  latch.countDown();
                }

                @Override
                public void onError(Throwable t) {
                  // do nothing
                }

                @Override
                public void onCompleted() {
                  // do nothing
                }
              });

      final ContractInvocation execution = contractInterface.newInvocationBuilder()
          .function(executeFunction)
          .args(new Object[] {executeKey, executeIntVal, executeStringVal})
          .build();

      execute(account, execution);
      execute(account, execution);

      subscription.unsubscribe();

      execute(account, execution);

      assertTrue(subscription.isUnsubscribed());
      assertEquals(0L, latch.getCount());
    }
  }

  @Test
  public void testLuaContractEventWithEventNameFilter() {
    for (final Account account : supplyAccounts()) {
      unlockAccount(account, password);
      final ContractDefinition definition = ContractDefinition.newBuilder()
          .encodedContract(contractPayload)
          .build();
      final ContractTxHash deployTxHash = deploy(account, definition);

      final ContractTxReceipt deployTxReceipt = getContractTxReceipt(deployTxHash);
      assertEquals("CREATED", deployTxReceipt.getStatus());

      final ContractAddress contractAddress = deployTxReceipt.getContractAddress();
      final ContractInterface contractInterface = getContractInterface(contractAddress);

      final EventFilter eventFilter = EventFilter.newBuilder(contractAddress)
          .eventName("set")
          .build();

      final AtomicInteger count = new AtomicInteger(2);
      final Subscription<Event> subscription =
          aergoClient.getContractOperation().subscribeEvent(eventFilter,
              new StreamObserver<Event>() {

                @Override
                public void onNext(Event value) {
                  count.decrementAndGet();
                }

                @Override
                public void onError(Throwable t) {
                  // do nothing
                }

                @Override
                public void onCompleted() {
                  // do nothing
                }
              });

      final ContractInvocation targetExec = contractInterface.newInvocationBuilder()
          .function(executeFunction)
          .args(new Object[] {executeKey, executeIntVal, executeStringVal})
          .build();

      final ContractInvocation otherExec = contractInterface.newInvocationBuilder()
          .function("set2")
          .args(new Object[] {randomUUID().toString(), randomUUID().toString().hashCode(),
              randomUUID().toString()})
          .build();

      execute(account, targetExec);
      execute(account, targetExec);

      execute(account, otherExec);
      execute(account, otherExec);
      execute(account, otherExec);

      subscription.unsubscribe();

      execute(account, targetExec);

      assertTrue(subscription.isUnsubscribed());
      assertEquals(0, count.get());
    }
  }

  @Test
  public void testLuaContractEventWithArgFilter() {
    for (final Account account : supplyAccounts()) {
      unlockAccount(account, password);
      final ContractDefinition definition = ContractDefinition.newBuilder()
          .encodedContract(contractPayload)
          .build();
      final ContractTxHash deployTxHash = deploy(account, definition);

      final ContractTxReceipt deployTxReceipt = getContractTxReceipt(deployTxHash);
      assertEquals("CREATED", deployTxReceipt.getStatus());

      final ContractAddress contractAddress = deployTxReceipt.getContractAddress();
      final ContractInterface contractInterface = getContractInterface(contractAddress);

      final EventFilter eventFilter = EventFilter.newBuilder(contractAddress)
          .args(asList(new Object[] {executeKey}))
          .build();

      final AtomicInteger count = new AtomicInteger(2);
      final Subscription<Event> subscription =
          aergoClient.getContractOperation().subscribeEvent(eventFilter,
              new StreamObserver<Event>() {

                @Override
                public void onNext(Event value) {
                  count.decrementAndGet();
                }

                @Override
                public void onError(Throwable t) {
                  // do nothing
                }

                @Override
                public void onCompleted() {
                  // do nothing
                }
              });

      final ContractInvocation targetExec = contractInterface.newInvocationBuilder()
          .function("set2")
          .args(new Object[] {executeKey, executeIntVal, executeStringVal})
          .build();

      final ContractInvocation otherExec = contractInterface.newInvocationBuilder()
          .function("set2")
          .args(new Object[] {randomUUID().toString(), executeIntVal, executeStringVal})
          .build();

      execute(account, targetExec);
      execute(account, targetExec);

      execute(account, otherExec);
      execute(account, otherExec);
      execute(account, otherExec);

      subscription.unsubscribe();

      execute(account, targetExec);

      assertTrue(subscription.isUnsubscribed());
      assertEquals(0, count.get());
    }
  }

  @Test
  public void testLuaContractEventWithEventNameAndArgFilter() {
    for (final Account account : supplyAccounts()) {
      unlockAccount(account, password);
      final ContractDefinition definition = ContractDefinition.newBuilder()
          .encodedContract(contractPayload)
          .build();
      final ContractTxHash deployTxHash = deploy(account, definition);

      final ContractTxReceipt deployTxReceipt = getContractTxReceipt(deployTxHash);
      assertEquals("CREATED", deployTxReceipt.getStatus());

      final ContractAddress contractAddress = deployTxReceipt.getContractAddress();
      final ContractInterface contractInterface = getContractInterface(contractAddress);

      final EventFilter eventFilter = EventFilter.newBuilder(contractAddress)
          .eventName("set")
          .args(asList(new Object[] {executeKey}))
          .build();

      final AtomicInteger count = new AtomicInteger(2);
      final Subscription<Event> subscription =
          aergoClient.getContractOperation().subscribeEvent(eventFilter,
              new StreamObserver<Event>() {

                @Override
                public void onNext(Event value) {
                  count.decrementAndGet();
                }

                @Override
                public void onError(Throwable t) {
                  // do nothing
                }

                @Override
                public void onCompleted() {
                  // do nothing
                }
              });

      final ContractInvocation targetExec = contractInterface.newInvocationBuilder()
          .function(executeFunction)
          .args(new Object[] {executeKey, executeIntVal, executeStringVal})
          .build();

      final ContractInvocation otherExec = contractInterface.newInvocationBuilder()
          .function(executeFunction)
          .args(new Object[] {randomUUID().toString(), executeIntVal, executeStringVal})
          .build();

      execute(account, targetExec);
      execute(account, targetExec);

      execute(account, otherExec);
      execute(account, otherExec);
      execute(account, otherExec);

      subscription.unsubscribe();

      execute(account, targetExec);

      assertTrue(subscription.isUnsubscribed());
      assertEquals(0, count.get());
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
