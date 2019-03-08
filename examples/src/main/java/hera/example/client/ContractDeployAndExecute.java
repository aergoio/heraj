/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.client;

import hera.api.model.Account;
import hera.api.model.AccountFactory;
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
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.example.AbstractExample;
import hera.key.AergoKeyGenerator;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class ContractDeployAndExecute extends AbstractExample {

  protected void deployAndExecute() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build();

    // create an account
    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());

    // funding account
    fund(account.getAddress());

    // read contract in a payload form
    final InputStream inputStream = getClass().getResourceAsStream("/payload");
    String encodedContract;
    Scanner scanner = new Scanner(inputStream, "UTF-8");
    try {
      encodedContract = scanner.useDelimiter("\\A").next();
    } finally {
      scanner.close();
    }

    // define contract definition
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(encodedContract)
        .constructorArgs("deployKey", 30, "init")
        .build();
    System.out.println("Contract definition: " + definition);

    // deploy contract definition
    final ContractTxHash deployTxHash = aergoClient.getContractOperation().deploy(account,
        definition, account.incrementAndGetNonce(), Fee.getDefaultFee());
    System.out.println("Deploy hash: " + deployTxHash);

    sleep(1500L);

    // query definition transaction receipt
    final ContractTxReceipt definitionReceipt =
        aergoClient.getContractOperation().getReceipt(deployTxHash);
    System.out.println("Deploy receipt: " + definitionReceipt);

    // get contract interface
    final ContractAddress contractAddress = definitionReceipt.getContractAddress();
    final ContractInterface contractInterface =
        aergoClient.getContractOperation().getContractInterface(contractAddress);
    System.out.println("Contract interface: " + contractInterface);

    // define contract execution
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function("set")
        .args("execKey", 70, "execute")
        .build();
    System.out.println("Contract invocation: " + execution);

    // execute
    final ContractTxHash executionTxHash = aergoClient.getContractOperation().execute(account,
        execution, account.incrementAndGetNonce(), Fee.getDefaultFee());
    System.out.println("Execution hash: " + executionTxHash);

    sleep(1500L);

    // query execution transaction receipt
    final ContractTxReceipt executionReceipt =
        aergoClient.getContractOperation().getReceipt(executionTxHash);
    System.out.println("Execution receipt: " + executionReceipt);

    // build query invocation for constructor
    final ContractInvocation queryForConstructor = contractInterface.newInvocationBuilder()
        .function("get")
        .args("deployKey")
        .build();
    System.out.println("Query invocation : " + queryForConstructor);

    // request query invocation
    final ContractResult deployResult =
        aergoClient.getContractOperation().query(queryForConstructor);
    System.out.println("Query result: " + deployResult);

    try {
      final Data data = deployResult.bind(Data.class);
      System.out.println("Binded result: " + data);
    } catch (Exception e) {
      throw new IllegalStateException("Binding error", e);
    }

    // build query invocation for execution
    final ContractInvocation queryForExecute = contractInterface.newInvocationBuilder()
        .function("get")
        .args("execKey")
        .build();
    System.out.println("Query invocation : " + queryForExecute);

    // request query invocation
    final ContractResult executeResult = aergoClient.getContractOperation().query(queryForExecute);
    System.out.println("Query result: " + deployResult);

    try {
      final Data data = executeResult.bind(Data.class);
      System.out.println("Binded result: " + data);
    } catch (Exception e) {
      throw new IllegalStateException("Binding error", e);
    }

    // close the client
    aergoClient.close();
  }

  protected void eventHandling() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build();

    // create an account
    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());

    // funding account
    fund(account.getAddress());

    // read contract in a payload form
    final InputStream inputStream = getClass().getResourceAsStream("/payload");
    String encodedContract;
    Scanner scanner = new Scanner(inputStream, "UTF-8");
    try {
      encodedContract = scanner.useDelimiter("\\A").next();
    } finally {
      scanner.close();
    }

    // define contract definition
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(encodedContract)
        .constructorArgs("deployKey", 30, "init")
        .build();
    System.out.println("Contract definition: " + definition);

    // deploy contract definition
    final ContractTxHash deployTxHash = aergoClient.getContractOperation().deploy(account,
        definition, account.incrementAndGetNonce(), Fee.getDefaultFee());
    System.out.println("Deploy hash: " + deployTxHash);

    sleep(1500L);

    // query definition transaction receipt
    final ContractTxReceipt definitionReceipt =
        aergoClient.getContractOperation().getReceipt(deployTxHash);
    System.out.println("Deploy receipt: " + definitionReceipt);

    // get contract interface
    final ContractAddress contractAddress = definitionReceipt.getContractAddress();
    final ContractInterface contractInterface =
        aergoClient.getContractOperation().getContractInterface(contractAddress);
    System.out.println("Contract interface: " + contractInterface);

    // event subscription
    final EventFilter eventFilter = EventFilter.newBuilder(contractAddress).build();
    final Subscription<Event> subscription =
        aergoClient.getContractOperation().subscribeEvent(eventFilter, new StreamObserver<Event>() {

          @Override
          public void onNext(Event value) {
            System.out.println("Received event: " + value);
          }

          @Override
          public void onError(Throwable t) {
            System.out.println("Received event error: " + t);
          }

          @Override
          public void onCompleted() {
            System.out.println("Event finished");
          }
        });
    System.out.println("Subscription status: " + !subscription.isUnsubscribed());

    // define & execute it twice
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function("set")
        .args("execKey", 70, "execute")
        .build();
    aergoClient.getContractOperation().execute(account, execution, account.incrementAndGetNonce(),
        Fee.getDefaultFee());
    aergoClient.getContractOperation().execute(account, execution, account.incrementAndGetNonce(),
        Fee.getDefaultFee());

    sleep(1500L);

    // unsubscribe event
    subscription.unsubscribe();
    System.out.println("Subscription status: " + !subscription.isUnsubscribed());

    // list event on recent 100 block. must be 2
    final EventFilter listEventFilter = EventFilter.newBuilder(contractAddress)
        .recentBlockCount(100)
        .build();
    final List<Event> events = aergoClient.getContractOperation().listEvents(listEventFilter);
    System.out.println("Event count on recent 100 block: " + events.size());

    // close the client
    aergoClient.close();
  }

  @Override
  public void run() {
    deployAndExecute();
    eventHandling();
  }

  public static void main(String[] args) {
    new ContractDeployAndExecute().run();
  }

}
