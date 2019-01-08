/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.lowlevel;

import hera.api.model.Account;
import hera.api.model.AccountFactory;
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
import hera.key.AergoKeyGenerator;
import java.io.InputStream;
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

    // read contract in a payload form
    final InputStream inputStream = getClass().getResourceAsStream("/payload");
    String encodedContract;
    Scanner scanner = new Scanner(inputStream, "UTF-8");
    try  {
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

  @Override
  public void run() {
    deployAndExecute();
  }

  public static void main(String[] args) {
    new ContractDeployAndExecute().run();
  }

}
