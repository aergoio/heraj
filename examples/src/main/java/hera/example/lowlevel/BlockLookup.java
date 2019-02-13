/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.lowlevel;

import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
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

  protected void blockHeaderLookup() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build();

    // query best block
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final BlockHash bestHash = status.getBestBlockHash();
    final long bestHeight = status.getBestHeight();

    // query block header corresponding to the block hash
    final BlockHeader blockHeaderByHash = aergoClient.getBlockOperation().getBlockHeader(bestHash);
    System.out.println("Block header by hash: " + blockHeaderByHash);

    // query block header corresponding to the block hash
    final BlockHeader blockHeaderByHeight =
        aergoClient.getBlockOperation().getBlockHeader(bestHeight);
    System.out.println("Block header by height: " + blockHeaderByHeight);

    // query 10 block headers starting from best block backward with best block hash
    final List<BlockHeader> blockHeadersByHash =
        aergoClient.getBlockOperation().listBlockHeaders(bestHash, 10);
    System.out.println("Block headers by hash: " + blockHeadersByHash);

    // query 10 block headers starting from best block backward with best block height
    final List<BlockHeader> blockHeadersByHeight =
        aergoClient.getBlockOperation().listBlockHeaders(bestHeight, 10);
    System.out.println("Block headers by height: " + blockHeadersByHeight);

    // close the client
    aergoClient.close();
  }

  @Override
  public void run() {
    blockLookup();
    blockHeaderLookup();
  }

  public static void main(String[] args) {
    new BlockLookup().run();
  }

}
