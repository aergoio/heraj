/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.wallet.transaction;

import hera.api.model.Authentication;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
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
import hera.key.AergoKey;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.KeyStore;
import hera.wallet.WalletApi;
import hera.wallet.WalletFactory;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class HandlingEvent extends AbstractExample {

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

    // event subscription
    final EventFilter eventFilter = EventFilter.newBuilder(contractAddress).build();
    final Subscription<Event> subscription =
        walletApi.queryApi().subscribeEvent(eventFilter, new StreamObserver<Event>() {

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
    walletApi.transactionApi().execute(execution, Fee.ZERO);
    walletApi.transactionApi().execute(execution, Fee.ZERO);

    Thread.sleep(2200L);

    // unsubscribe event
    subscription.unsubscribe();
    System.out.println("Subscription status: " + !subscription.isUnsubscribed());

    // list event on recent 100 block. must be 2
    final EventFilter listEventFilter = EventFilter.newBuilder(contractAddress)
        .recentBlockCount(100)
        .build();
    final List<Event> events = walletApi.queryApi().listEvents(listEventFilter);
    System.out.println("Event count on recent 100 block: " + events.size());

    // lock an wallet
    walletApi.lock(authentication);

    // close the client
    aergoClient.close();
  }

  public static void main(String[] args) throws Exception {
    new HandlingEvent().run();
  }

}
