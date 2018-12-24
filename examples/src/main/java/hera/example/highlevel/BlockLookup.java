/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.highlevel;

import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.example.AbstractExample;
import hera.wallet.Wallet;
import hera.wallet.WalletBuilder;
import hera.wallet.WalletType;
import java.util.List;

public class BlockLookup extends AbstractExample {

  protected void blockLookup() {
    // make wallet object
    final Wallet wallet = new WalletBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build(WalletType.Naive);

    // get best block hash, height
    final BlockHash bestHash = wallet.getBestBlockHash();
    final long bestHeight = wallet.getBestBlockHeight();

    // lookup block by best block hash
    final Block blockByHash = wallet.getBlock(bestHash);
    System.out.println("Block by hash: " + blockByHash);

    // lookup block by best height
    final Block blockByHeight = wallet.getBlock(bestHeight);
    System.out.println("Block by height: " + blockByHeight);

    // lookup previous block by hash
    final Block previousBlock =
        wallet.getBlock(blockByHash.getPreviousHash());
    System.out.println("Previous block: " + previousBlock);

    // close the wallet
    wallet.close();
  }

  protected void blockHeaderLookup() {
    // make aergo client object
    final Wallet wallet = new WalletBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build(WalletType.Naive);

    // get best block hash, height
    final BlockHash bestHash = wallet.getBestBlockHash();
    final Block block = wallet.getBlock(bestHash);

    // lookup 10 block headers starting from best block backward with best block hash
    final List<BlockHeader> blockHeadersByHash =
        wallet.listBlockHeaders(block.getHash(), 10);
    System.out.println("Block headers by hash: " + blockHeadersByHash);

    // lookup 10 block headers starting from best block backward with best block height
    final List<BlockHeader> blockHeadersByHeight =
        wallet.listBlockHeaders(block.getBlockNumber(), 10);
    System.out.println("Block headers by height: " + blockHeadersByHeight);

    // close the client
    wallet.close();
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
