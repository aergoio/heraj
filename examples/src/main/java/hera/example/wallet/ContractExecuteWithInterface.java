/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.wallet;

import static java.util.UUID.randomUUID;

import hera.api.model.Authentication;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.contract.SmartContractFactory;
import hera.example.AbstractExample;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.wallet.Wallet;
import hera.wallet.WalletBuilder;
import hera.wallet.WalletType;
import java.io.InputStream;
import java.util.Scanner;

public class ContractExecuteWithInterface extends AbstractExample {

  protected void deployAndExecute() {
    // make aergo client object
    final Wallet wallet = new WalletBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build(WalletType.Naive);

    // create an account
    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();

    // funding account
    fund(key.getAddress());

    // save and unlock it
    wallet.saveKey(key, password);
    wallet.unlock(Authentication.of(key.getAddress(), password));

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
    final ContractTxHash deployTxHash = wallet.deploy(definition);
    System.out.println("Deploy hash: " + deployTxHash);

    sleep(1500L);

    // query definition transaction receipt
    final ContractTxReceipt definitionReceipt = wallet.getReceipt(deployTxHash);
    System.out.println("Deploy receipt: " + definitionReceipt);

    // get contract interface
    final ContractAddress contractAddress = definitionReceipt.getContractAddress();

    // make dynamic proxy with interface
    final SmartContractInterface contract =
        new SmartContractFactory().create(SmartContractInterface.class, contractAddress);
    contract.bind(wallet);

    contract.set("execKey", 70, "execute");

    sleep(1500L);

    // request query invocation
    final Data deployResult = contract.get("deployKey");
    System.out.println("Deploy result: " + deployResult);

    // request query invocation
    final Data executeResult = contract.get("executeKey");
    System.out.println("Execute result: " + executeResult);

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

