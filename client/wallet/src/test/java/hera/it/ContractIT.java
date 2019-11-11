/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.Fee;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.exception.WalletException;
import hera.util.IoUtils;
import hera.util.Pair;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.Test;

public class ContractIT extends AbstractWalletApiIT {

  protected final String execFunction = "set";
  protected final String execFunctionEvent = "set";
  protected final String execFunction2 = "set2";
  protected final String execFunction2Event = "set2";
  protected final String queryFunction = "get";

  @Test
  public void shouldDeployOnUnlocked() throws IOException {
    // when
    walletApi.unlock(authentication);
    final String payload = IoUtils.from(new InputStreamReader(open("payload")));
    final Fee fee = Fee.EMPTY;

    // then
    final ContractInterface contractInterface = deploy(payload, fee);
    assertNotNull(contractInterface);
  }

  @Test
  public void shouldDeployFailOnLocked() throws IOException {
    // when
    final String payload = IoUtils.from(new InputStreamReader(open("payload")));
    final Fee fee = Fee.EMPTY;

    // then
    try {
      deploy(payload, fee);
      fail();
    } catch (WalletException e) {
      // good we expected this
    }
  }

  @Test
  public void shouldValueSetOnDeployWithArgs() throws IOException {
    // when
    walletApi.unlock(authentication);
    final String payload = IoUtils.from(new InputStreamReader(open("payload")));
    final Fee fee = Fee.EMPTY;
    final String key = randomUUID().toString();
    final int intVal = randomUUID().hashCode();
    final String stringVal = randomUUID().toString();

    // then
    final ContractInterface contractInterface = deploy(payload, fee, key, intVal, stringVal);
    final ContractResult result = query(contractInterface, queryFunction, key);
    final Data data = result.bind(Data.class);
    assertEquals(intVal, data.getIntVal());
    assertEquals(stringVal, data.getStringVal());
  }

  @Test
  public void shouldExecuteOnUnlocked() throws IOException {
    // when
    walletApi.unlock(authentication);
    final String payload = IoUtils.from(new InputStreamReader(open("payload")));
    final Fee deployFee = Fee.EMPTY;
    final ContractInterface contractInterface = deploy(payload, deployFee);

    // then
    final String key = randomUUID().toString();
    final int intVal = randomUUID().hashCode();
    final String stringVal = randomUUID().toString();
    final Fee execFee = Fee.EMPTY;
    execute(contractInterface, execFunction, execFee, key, intVal, stringVal);
    final ContractResult result = query(contractInterface, queryFunction, key);
    final Data data = result.bind(Data.class);
    assertEquals(intVal, data.getIntVal());
    assertEquals(stringVal, data.getStringVal());
  }

  @Test
  public void shouldExecuteFailOnLocked() throws IOException {
    // when
    walletApi.unlock(authentication);
    final String payload = IoUtils.from(new InputStreamReader(open("payload")));
    final Fee deployFee = Fee.EMPTY;
    final ContractInterface contractInterface = deploy(payload, deployFee);
    walletApi.lock(authentication);

    // then
    final String key = randomUUID().toString();
    final int intVal = randomUUID().hashCode();
    final String stringVal = randomUUID().toString();
    final Fee execFee = Fee.EMPTY;
    try {
      execute(contractInterface, execFunction, execFee, key, intVal, stringVal);
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void shouldSubscribeExecEvent() throws IOException {
    // when
    walletApi.unlock(authentication);
    final String payload = IoUtils.from(new InputStreamReader(open("payload")));
    final Fee deployFee = Fee.EMPTY;
    final ContractInterface contractInterface = deploy(payload, deployFee);

    final int tryCount = 5;
    final Collection<Pair<String, Pair<Integer, String>>> execArgs = generateArgs(tryCount);
    final EventFilter filter = EventFilter.newBuilder(contractInterface.getAddress())
        .build();
    final Subscription<Event> subsription =
        walletApi.queryApi().subscribeEvent(filter, new StreamObserver<Event>() {

          @Override
          public void onNext(Event value) {
            List<Object> rawArgs = value.getArgs();
            final Pair<String, Pair<Integer, String>> recovered = recoverArgs(rawArgs);
            execArgs.remove(recovered);
          }

          @Override
          public void onError(Throwable t) {
            // TODO Auto-generated method stub

          }

          @Override
          public void onCompleted() {
            // TODO Auto-generated method stub

          }
        });

    // then
    final Fee execFee = Fee.EMPTY;
    final Collection<Pair<String, Pair<Integer, String>>> clone = new HashSet<>(execArgs);
    for (final Pair<String, Pair<Integer, String>> next : clone) {
      execute(contractInterface, execFunction, execFee, next.v1, next.v2.v1, next.v2.v2);
    }
    assertEquals(0, execArgs.size());
    subsription.unsubscribe();
  }

  @Test
  public void shouldFilterEventByFuncName() throws IOException {
    // when
    walletApi.unlock(authentication);
    final String payload = IoUtils.from(new InputStreamReader(open("payload")));
    final Fee deployFee = Fee.EMPTY;
    final ContractInterface contractInterface = deploy(payload, deployFee);

    final int event1Try = 2;
    final int event2Try = 4;

    final AtomicInteger count = new AtomicInteger(event1Try);
    final EventFilter filter = EventFilter.newBuilder(contractInterface.getAddress())
        .eventName(execFunctionEvent)
        .build();
    final Subscription<Event> subsription =
        walletApi.queryApi().subscribeEvent(filter, new StreamObserver<Event>() {

          @Override
          public void onNext(Event value) {
            count.decrementAndGet();
          }

          @Override
          public void onError(Throwable t) {
            // TODO Auto-generated method stub

          }

          @Override
          public void onCompleted() {
            // TODO Auto-generated method stub

          }
        });

    // then
    final Fee execFee = Fee.EMPTY;
    for (int i = 0; i < event1Try; ++i) {
      execute(contractInterface, execFunction, execFee, randomUUID().toString(),
          randomUUID().hashCode(), randomUUID().toString());
    }
    for (int i = 0; i < event2Try; ++i) {
      execute(contractInterface, execFunction2, execFee, randomUUID().toString(),
          randomUUID().hashCode(), randomUUID().toString());
    }
    assertEquals(0, count.get());
    subsription.unsubscribe();

  }


  protected Collection<Pair<String, Pair<Integer, String>>> generateArgs(final int tryCount) {
    final Collection<Pair<String, Pair<Integer, String>>> execArgs = new HashSet<>();
    for (int i = 0; i < tryCount; ++i) {
      final Pair<Integer, String> value =
          new Pair<>(randomUUID().hashCode(), randomUUID().toString());
      final Pair<String, Pair<Integer, String>> keyAndValue =
          new Pair<>(randomName().toString(), value);
      execArgs.add(keyAndValue);
    }
    return execArgs;
  }

  protected Pair<String, Pair<Integer, String>> recoverArgs(List<Object> rawArgs) {
    final Pair<Integer, String> recoveredValue =
        new Pair<>((Integer) rawArgs.get(1), (String) rawArgs.get(2));
    final Pair<String, Pair<Integer, String>> recoveredKeyAndValue =
        new Pair<>((String) rawArgs.get(0), recoveredValue);
    return recoveredKeyAndValue;
  }

  protected ContractInterface deploy(final String payload, final Fee fee, final Object... args) {
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payload)
        .constructorArgs(args)
        .build();
    final ContractTxHash deployTxHash = walletApi.transactionApi().deploy(definition, fee);
    waitForNextBlockToGenerate();

    final ContractTxReceipt receipt = walletApi.queryApi().getReceipt(deployTxHash);
    final ContractAddress contractAddress = receipt.getContractAddress();
    final ContractInterface contractInterface =
        walletApi.queryApi().getContractInterface(contractAddress);
    return contractInterface;
  }

  protected ContractTxReceipt execute(final ContractInterface contractInterface,
      final String funcName,
      final Fee fee, final Object... args) {
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function(funcName)
        .args(args)
        .build();
    final ContractTxHash execTxHash = walletApi.transactionApi().execute(execution, fee);
    waitForNextBlockToGenerate();
    return walletApi.queryApi().getReceipt(execTxHash);
  }

  protected ContractResult query(final ContractInterface contractInterface, final String funcName,
      final Object... args) {
    final ContractInvocation query = contractInterface.newInvocationBuilder()
        .function(funcName)
        .args(args)
        .build();
    return walletApi.queryApi().query(query);
  }

  @ToString
  protected static class Data {

    @Getter
    @Setter
    protected int intVal;

    @Getter
    @Setter
    protected String stringVal;

  }

}
