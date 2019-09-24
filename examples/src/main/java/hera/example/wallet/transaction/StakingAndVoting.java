/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.wallet.transaction;

import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.StakeInfo;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.example.AbstractExample;
import hera.key.AergoKey;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.KeyStore;
import hera.wallet.WalletApi;
import hera.wallet.WalletFactory;
import java.util.ArrayList;
import java.util.List;

public class StakingAndVoting extends AbstractExample {

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

    // stake aergo
    TxHash stakeTxHash = walletApi.transactionApi().stake(Aer.of("10000", Unit.AERGO));
    System.out.println("Staking TxHash: " + stakeTxHash);

    // sleep
    Thread.sleep(2200L);

    // get stake info
    StakeInfo stakeInfo = walletApi.queryApi().getStakingInfo(walletApi.getPrincipal());
    System.out.println("Stake info: " + stakeInfo);

    // vote
    List<String> candidates = new ArrayList<>();
    candidates.add("16Uiu2HAmRSPHJYKjAx7fJvwxTVJyLBLhV2rTcq9b6SLPvVnJJ45o");
    TxHash voteTxHash = walletApi.transactionApi().voteBp(candidates);
    System.out.println("Voting TxHash: " + voteTxHash);

    // lock an wallet
    walletApi.lock(authentication);

    // close the client
    aergoClient.close();
  }

  public static void main(String[] args) throws Exception {
    new StakingAndVoting().run();
  }

}
