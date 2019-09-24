/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.wallet.query;

import hera.api.model.Block;
import hera.api.model.BlockMetadata;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.example.AbstractExample;
import hera.keystore.InMemoryKeyStore;
import hera.wallet.WalletApi;
import hera.wallet.WalletFactory;

public class SubscribingBlock extends AbstractExample {

  @Override
  public void run() throws Exception {
    // make wallet api
    WalletApi walletApi = new WalletFactory().create(new InMemoryKeyStore());

    // make aergo client
    AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build();

    // bind aergo client
    walletApi.bind(aergoClient);

    // subscribe new block metadata
    Subscription<BlockMetadata> metadataSubscription =
        walletApi.queryApi().subscribeNewBlockMetadata(new StreamObserver<BlockMetadata>() {

          @Override
          public void onNext(BlockMetadata value) {
            System.out.println("Next block metadata: " + value);
          }

          @Override
          public void onError(Throwable t) {}

          @Override
          public void onCompleted() {}
        });

    // sleep
    Thread.sleep(3000L);

    // unsubscribe it
    metadataSubscription.unsubscribe();


    Subscription<Block> blockSubscription =
        walletApi.queryApi().subscribeNewBlock(new StreamObserver<Block>() {

          @Override
          public void onNext(Block value) {
            System.out.println("Next block: " + value);
          }

          @Override
          public void onError(Throwable t) {}

          @Override
          public void onCompleted() {}
        });

    // sleep
    Thread.sleep(3000L);

    // unsubscribe it
    blockSubscription.unsubscribe();

    // close the client
    aergoClient.close();
  }

  public static void main(String[] args) throws Exception {
    new SubscribingBlock().run();
  }

}
