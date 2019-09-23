/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.wallet.query;

import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockMetadata;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.example.AbstractExample;
import hera.keystore.InMemoryKeyStore;
import hera.wallet.WalletApi;
import hera.wallet.WalletFactory;
import java.util.List;

public class QueryingBlockInfo extends AbstractExample {

  @Override
  public void run() {
    // make wallet api
    WalletApi walletApi = new WalletFactory().create(new InMemoryKeyStore());

    // make aergo client
    AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build();

    // bind aergo client
    walletApi.bind(aergoClient);

    // get best block hash, height
    BlockHash bestHash = walletApi.queryApi().getBestBlockHash();
    long bestHeight = walletApi.queryApi().getBestBlockHeight();
    System.out.printf("Best block hash: %s, height: %d\n", bestHeight, bestHeight);

    // query block by best block hash and height
    Block blockByHash = walletApi.queryApi().getBlock(bestHash);
    Block blockByHeight = walletApi.queryApi().getBlock(bestHeight);
    System.out.printf("Best block by hash: %s, by height: %s\n", blockByHash, blockByHeight);

    // query block metadata by hash, height
    BlockMetadata metatdataByHash = walletApi.queryApi().getBlockMetadata(bestHash);
    BlockMetadata metatdataByHeight = walletApi.queryApi().getBlockMetadata(bestHeight);
    System.out.printf("Metadata by hash: %s, by height: %s\n", metatdataByHash, metatdataByHeight);

    // query 10 block metadatas starting from best block to the backward
    List<BlockMetadata> metadatasByHash = walletApi.queryApi().listBlockMetadatas(bestHash, 10);
    List<BlockMetadata> metadatasByHeight = walletApi.queryApi().listBlockMetadatas(bestHeight, 10);
    System.out.printf("Metadatas by hash: %s, by height: %s\n", metadatasByHash, metadatasByHeight);

    // close the client
    aergoClient.close();
  }

  public static void main(String[] args) {
    new QueryingBlockInfo().run();
  }

}
