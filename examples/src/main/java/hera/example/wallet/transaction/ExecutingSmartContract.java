/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.wallet.transaction;

import hera.api.model.Authentication;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Fee;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.example.AbstractExample;
import hera.key.AergoKey;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.KeyStore;
import hera.wallet.WalletApi;
import hera.wallet.WalletFactory;
import java.io.InputStream;
import java.util.Scanner;

public class ExecutingSmartContract extends AbstractExample {

  @Override
  public void run() throws Exception {
    // make keystore and save key
    KeyStore keyStore = new InMemoryKeyStore();
    AergoKey key = supplyKey();
    Authentication authentication = Authentication.of(key.getAddress(), "password");
    keyStore.save(authentication, key);

    // make wallet api
    WalletApi walletApi = new WalletFactory().create(keyStore);

    // make aergo client
    AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build();

    // bind aergo client
    walletApi.bind(aergoClient);

    // unlock account
    walletApi.unlock(authentication);

    // read contract in a payload form
    InputStream inputStream = getClass().getResourceAsStream("/payload");
    String encodedContract;
    try (Scanner scanner = new Scanner(inputStream, "UTF-8")) {
      encodedContract = scanner.useDelimiter("\\A").next();
    }

    // define contract definition
    ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(encodedContract)
        .constructorArgs("deployKey", 30, "init")
        .build();
    System.out.println("Contract definition: " + definition);

    // deploy contract definition
    ContractTxHash deployTxHash = walletApi.transactionApi().deploy(definition, Fee.ZERO);
    System.out.println("Deploy hash: " + deployTxHash);

    Thread.sleep(2200L);

    // query definition transaction receipt
    ContractTxReceipt definitionReceipt = walletApi.queryApi().getReceipt(deployTxHash);
    System.out.println("Deploy receipt: " + definitionReceipt);

    // get contract interface
    ContractAddress contractAddress = definitionReceipt.getContractAddress();
    ContractInterface contractInterface =
        walletApi.queryApi().getContractInterface(contractAddress);
    System.out.println("Contract interface: " + contractInterface);

    // define contract execution
    ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function("set")
        .args("execKey", 70, "execute")
        .build();
    System.out.println("Contract invocation: " + execution);

    // execute
    ContractTxHash executionTxHash = walletApi.transactionApi().execute(execution, Fee.ZERO);
    System.out.println("Execution hash: " + executionTxHash);

    Thread.sleep(2200L);

    // query execution transaction receipt
    ContractTxReceipt executionReceipt = walletApi.queryApi().getReceipt(executionTxHash);
    System.out.println("Execution receipt: " + executionReceipt);

    // build query invocation for constructor
    ContractInvocation queryForConstructor = contractInterface.newInvocationBuilder()
        .function("get")
        .args("deployKey")
        .build();
    System.out.println("Query invocation : " + queryForConstructor);

    // request query invocation
    ContractResult deployResult = walletApi.queryApi().query(queryForConstructor);
    System.out.println("Query result: " + deployResult);

    try {
      Data data = deployResult.bind(Data.class);
      System.out.println("Binded result: " + data);
    } catch (Exception e) {
      throw new IllegalStateException("Binding error", e);
    }

    // build query invocation for execution
    ContractInvocation queryForExecute = contractInterface.newInvocationBuilder()
        .function("get")
        .args("execKey")
        .build();
    System.out.println("Query invocation : " + queryForExecute);

    // request query invocation
    ContractResult executeResult = walletApi.queryApi().query(queryForExecute);
    System.out.println("Query result: " + deployResult);

    try {
      Data data = executeResult.bind(Data.class);
      System.out.println("Binded result: " + data);
    } catch (Exception e) {
      throw new IllegalStateException("Binding error", e);
    }

    // lock an wallet
    walletApi.lock(authentication);

    // close the client
    aergoClient.close();
  }

  public static void main(String[] args) throws Exception {
    new ExecutingSmartContract().run();
  }

}
