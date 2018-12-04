/*
 * @copyright defined in LICENSE.txt
 */

package hera.example;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
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
import hera.api.model.Fee;
import hera.api.model.ServerManagedAccount;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import java.io.InputStream;
import java.util.Scanner;
import lombok.Getter;
import lombok.Setter;

public class ContractExample extends AbstractExample {

  @Setter
  @Getter
  protected ContractInterface contractInterface;

  @Setter
  @Getter
  protected Account account;

  protected void deployWithKey() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint("localhost:7845")
        .withNonBlockingConnect()
        .build();

    // create an account
    final AergoKey key = AergoKey.of(
        "47pArdc5PNS9HYY9jMMC7zAuHzytzsAuCYGm5jAUFuD3amQ4mQkvyUaPnmRVSPc2iWzVJpC9Z", "password");
    final Account account = ClientManagedAccount.of(key);
    final AccountState state = aergoClient.getAccountOperation().getState(account);
    account.bindState(state);

    // read contract in a payload form
    final InputStream inputStream = getClass().getResourceAsStream("/payload");
    String contractPayload;
    try (Scanner scanner = new Scanner(inputStream, "UTF-8")) {
      contractPayload = scanner.useDelimiter("\\A").next();
    }

    // define contract definition
    final ContractDefinition definition =
        new ContractDefinition(() -> contractPayload, "initKey", 30, "init");
    System.out.println("Contract definition: " + definition);

    // deploy contract definition
    final ContractTxHash deployTxHash =
        aergoClient.getContractOperation().deploy(account, definition, Fee.of(1L, 1L));
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
    setContractInterface(contractInterface);
    System.out.println("Contract interface: " + contractInterface);

    // close the client
    aergoClient.close();
  }

  protected void deployWithPassword() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint("localhost:7845")
        .withNonBlockingConnect()
        .build();

    // create an account
    final Account account = ServerManagedAccount
        .of(AccountAddress.of(() -> "AmM25FKSK1gCqSdUPjnvESsauESNgfZUauHWp7R8Un3zHffEQgTm"));
    final AccountState state = aergoClient.getAccountOperation().getState(account);
    account.bindState(state);

    // unlock first
    aergoClient.getKeyStoreOperation().unlock(Authentication.of(account.getAddress(), "password"));

    // read contract in a payload form
    final InputStream inputStream = getClass().getResourceAsStream("/payload");
    String contractPayload;
    try (Scanner scanner = new Scanner(inputStream, "UTF-8")) {
      contractPayload = scanner.useDelimiter("\\A").next();
    }

    // define contract definition
    final ContractDefinition definition =
        new ContractDefinition(() -> contractPayload, "initKey", 30, "init");
    System.out.println("Contract definition: " + definition);

    // deploy contract definition
    final ContractTxHash deployTxHash =
        aergoClient.getContractOperation().deploy(account, definition, Fee.of(1L, 1L));
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
    setContractInterface(contractInterface);
    System.out.println("Contract interface: " + contractInterface);

    // lock after use an account
    aergoClient.getKeyStoreOperation().lock(Authentication.of(account.getAddress(), "password"));

    // close the client
    aergoClient.close();
  }

  protected void executeWithKey() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint("localhost:7845")
        .withNonBlockingConnect()
        .build();

    // create an account
    final AergoKey key = AergoKey.of(
        "47pArdc5PNS9HYY9jMMC7zAuHzytzsAuCYGm5jAUFuD3amQ4mQkvyUaPnmRVSPc2iWzVJpC9Z", "password");
    final Account account = ClientManagedAccount.of(key);
    final AccountState state = aergoClient.getAccountOperation().getState(account);
    account.bindState(state);

    // define contract execution
    final ContractFunction executionFunction = contractInterface.findFunction("set").get();
    final ContractInvocation execution = ContractInvocation
        .of(contractInterface.getContractAddress(), executionFunction, "key", 70, "execute");
    System.out.println("Contract invocation: " + execution);

    // execute the invocation
    final ContractTxHash executionTxHash =
        aergoClient.getContractOperation().execute(account, execution, Fee.of(1L, 1L));
    System.out.println("Execution hash: " + executionTxHash);

    sleep(1500L);

    // query execution transaction receipt
    final ContractTxReceipt executionReceipt =
        aergoClient.getContractOperation().getReceipt(executionTxHash);
    System.out.println("Execution receipt: " + executionReceipt);

    // close the client
    aergoClient.close();
  }

  protected void executeWithPassword() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint("localhost:7845")
        .withNonBlockingConnect()
        .build();

    // create an account
    final Account account = ServerManagedAccount
        .of(AccountAddress.of(() -> "AmM25FKSK1gCqSdUPjnvESsauESNgfZUauHWp7R8Un3zHffEQgTm"));
    final AccountState state = aergoClient.getAccountOperation().getState(account);
    account.bindState(state);

    // unlock first
    aergoClient.getKeyStoreOperation().unlock(Authentication.of(account.getAddress(), "password"));

    // define contract execution
    final ContractFunction executionFunction = contractInterface.findFunction("set").get();
    final ContractInvocation execution = ContractInvocation
        .of(contractInterface.getContractAddress(), executionFunction, "key", 70, "execute");
    System.out.println("Contract invocation: " + execution);

    // execute the invocation
    final ContractTxHash executionTxHash =
        aergoClient.getContractOperation().execute(account, execution, Fee.of(1L, 1L));
    System.out.println("Execution hash: " + executionTxHash);

    sleep(1500L);

    // query execution transaction receipt
    final ContractTxReceipt executionReceipt =
        aergoClient.getContractOperation().getReceipt(executionTxHash);
    System.out.println("Execution receipt: " + executionReceipt);

    // lock after use an account
    aergoClient.getKeyStoreOperation().lock(Authentication.of(account.getAddress(), "password"));

    // close the client
    aergoClient.close();
  }

  protected void queryInit() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint("localhost:7845")
        .withNonBlockingConnect()
        .build();

    // build query invocation
    final ContractFunction queryFunction = contractInterface.findFunction("get").get();
    final ContractInvocation query =
        ContractInvocation.of(contractInterface.getContractAddress(), queryFunction, "initKey");
    System.out.println("Query invocation : " + query);

    // request query invocation
    final ContractResult queryResult = aergoClient.getContractOperation().query(query);
    System.out.println("Query result: " + queryResult);

    try {
      final Data data = queryResult.bind(Data.class);
      System.out.println("Binded result: " + data);
    } catch (Exception e) {
      throw new IllegalStateException("Binding error", e);
    }

    // close the client
    aergoClient.close();
  }

  protected void queryExecute() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint("localhost:7845")
        .withNonBlockingConnect()
        .build();

    // build query invocation
    final ContractFunction queryFunction = contractInterface.findFunction("get").get();
    final ContractInvocation query =
        ContractInvocation.of(contractInterface.getContractAddress(), queryFunction, "key");
    System.out.println("Query invocation : " + query);

    // request query invocation
    final ContractResult queryResult = aergoClient.getContractOperation().query(query);
    System.out.println("Query result: " + queryResult);

    try {
      final Data data = queryResult.bind(Data.class);
      System.out.println("Binded result: " + data);
    } catch (Exception e) {
      throw new IllegalStateException("Binding error", e);
    }

    // close the client
    aergoClient.close();
  }

  @Override
  public void run() {
    deployWithKey();
    queryInit();
    executeWithKey();
    queryExecute();

    deployWithPassword();
    queryInit();
    executeWithPassword();
    queryExecute();
  }

  public static void main(String[] args) {
    new ContractExample().run();
  }

}
