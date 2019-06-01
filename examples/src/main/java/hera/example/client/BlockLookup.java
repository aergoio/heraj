/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.client;

import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockMetadata;
import hera.api.model.BlockchainStatus;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.example.AbstractExample;
import java.util.List;

public class BlockLookup extends AbstractExample {

  protected void blockLookup() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build();

    // query current blockchain status
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();

    // query block by best block hash
    final Block blockByHash = aergoClient.getBlockOperation().getBlock(status.getBestBlockHash());
    System.out.println("Block by hash: " + blockByHash);

    // query block by best height
    final Block blockByHeight = aergoClient.getBlockOperation().getBlock(status.getBestHeight());
    System.out.println("Block by height: " + blockByHeight);

    // query previous block by hash
    final Block previousBlock =
        aergoClient.getBlockOperation().getBlock(blockByHash.getPreviousHash());
    System.out.println("Previous block: " + previousBlock);

    // close the client
    aergoClient.close();
  }

  protected void blockMetadataLookup() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build();

    // query best block
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final BlockHash bestHash = status.getBestBlockHash();
    final long bestHeight = status.getBestHeight();

    // query block metadata corresponding to the block hash
    final BlockMetadata blockMetadataByHash =
        aergoClient.getBlockOperation().getBlockMetadata(bestHash);
    System.out.println("Block metadata by hash: " + blockMetadataByHash);

    // query block metadata corresponding to the block hash
    final BlockMetadata blockMetadataByHeight =
        aergoClient.getBlockOperation().getBlockMetadata(bestHeight);
    System.out.println("Block metadata by height: " + blockMetadataByHeight);

    // query 10 block metadatas starting from best block backward with best block hash
    final List<BlockMetadata> blockMetadatasByHash =
        aergoClient.getBlockOperation().listBlockMetadatas(bestHash, 10);
    System.out.println("Block metadatas by hash: " + blockMetadatasByHash);

    // query 10 block metadatas starting from best block backward with best block height
    final List<BlockMetadata> blockMetadatasByHeight =
        aergoClient.getBlockOperation().listBlockMetadatas(bestHeight, 10);
    System.out.println("Block metadatas by height: " + blockMetadatasByHeight);

    // close the client
    aergoClient.close();
  }

  @Override
  public void run() {
    blockLookup();
    blockMetadataLookup();
  }

  public static void main(String[] args) {
    new BlockLookup().run();
  }

}
