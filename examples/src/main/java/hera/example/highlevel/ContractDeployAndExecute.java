/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.highlevel;

import static java.util.UUID.randomUUID;

import hera.api.model.Authentication;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Fee;
import hera.example.AbstractExample;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.wallet.Wallet;
import hera.wallet.WalletBuilder;
import hera.wallet.WalletType;
import java.io.InputStream;
import java.util.Scanner;

public class ContractDeployAndExecute extends AbstractExample {

  protected void deployAndExecute() {
    // make aergo client object
    final Wallet wallet = new WalletBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build(WalletType.Naive);

    // create an account
    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();

    // save and unlock it
    wallet.saveKey(key, password);
    wallet.unlock(Authentication.of(key.getAddress(), password));

    // read contract in a payload form
    final InputStream inputStream = getClass().getResourceAsStream("/payload");
    String encodedContract;
    try (Scanner scanner = new Scanner(inputStream, "UTF-8")) {
      encodedContract = scanner.useDelimiter("\\A").next();
    }

    // define contract definition
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(encodedContract)
        .constructorArgs("deployKey", 30, "init")
        .build();
    System.out.println("Contract definition: " + definition);

    // deploy contract definition
    final ContractTxHash deployTxHash = wallet.deploy(definition, Fee.getDefaultFee());
    System.out.println("Deploy hash: " + deployTxHash);

    sleep(1500L);

    // query definition transaction receipt
    final ContractTxReceipt definitionReceipt = wallet.getReceipt(deployTxHash);
    System.out.println("Deploy receipt: " + definitionReceipt);

    // get contract interface
    final ContractAddress contractAddress = definitionReceipt.getContractAddress();
    final ContractInterface contractInterface = wallet.getContractInterface(contractAddress);
    System.out.println("Contract interface: " + contractInterface);

    // define contract execution
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function("set")
        .args("execKey", 70, "execute")
        .build();
    System.out.println("Contract invocation: " + execution);

    // execute
    final ContractTxHash executionTxHash = wallet.execute(execution, Fee.getDefaultFee());
    System.out.println("Execution hash: " + executionTxHash);

    sleep(1500L);

    // query execution transaction receipt
    final ContractTxReceipt executionReceipt = wallet.getReceipt(executionTxHash);
    System.out.println("Execution receipt: " + executionReceipt);

    // build query invocation for constructor
    final ContractInvocation queryForConstructor = contractInterface.newInvocationBuilder()
        .function("get")
        .args("deployKey")
        .build();
    System.out.println("Query invocation : " + queryForConstructor);

    // request query invocation
    final ContractResult deployResult = wallet.query(queryForConstructor);
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
    final ContractResult executeResult = wallet.query(queryForExecute);
    System.out.println("Query result: " + deployResult);

    try {
      final Data data = executeResult.bind(Data.class);
      System.out.println("Binded result: " + data);
    } catch (Exception e) {
      throw new IllegalStateException("Binding error", e);
    }

    // close the wallet
    wallet.close();
  }

  @Override
  public void run() {
    deployAndExecute();
  }

  public static void main(String[] args) {
    new ContractDeployAndExecute().run();
  }

}
