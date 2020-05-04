/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;

import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Authentication;
import hera.api.model.Fee;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.KeyStore;
import hera.wallet.WalletApi;
import hera.wallet.WalletApiFactory;
import java.io.IOException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@BenchmarkMode(Mode.Throughput)
public class WalletApiBenchmark {

  @State(Scope.Thread)
  public static class User {

    protected WalletApi walletApi;

    protected AergoClient client;

    protected AccountAddress recipient = new AergoKeyGenerator().create().getAddress();

    @Setup(Level.Trial)
    public synchronized void setUp() {
      KeyStore keyStore = new InMemoryKeyStore();
      final AergoKey key = new AergoKeyGenerator().create();
      final Authentication auth = Authentication.of(key.getAddress(), randomUUID().toString());
      keyStore.save(auth, key);

      client = new AergoClientBuilder().withEndpoint("localhost:7845").build();

      walletApi = new WalletApiFactory().create(keyStore);
      walletApi.unlock(auth);
    }

    public void send() {
      walletApi.with(client).transaction().send(recipient, Aer.ONE, Fee.EMPTY);
    }

    @TearDown(Level.Trial)
    public synchronized void tearDown() throws IOException {
      client.close();
    }

  }

  @Benchmark
  public void send(final User user) {
    user.send();
  }

}


