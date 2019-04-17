/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import hera.api.model.Account;
import hera.api.model.AccountFactory;
import hera.api.model.Aer;
import hera.api.model.Authentication;
import hera.api.model.Fee;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.wallet.Wallet;
import hera.wallet.WalletBuilder;
import hera.wallet.WalletType;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@BenchmarkMode(Mode.Throughput)
public class WalletBenchmark {

  @State(Scope.Thread)
  public static class User {

    protected Wallet wallet;

    protected Account recipient;

    @Setup(Level.Trial)
    public synchronized void setUp() {
      wallet = new WalletBuilder()
          .withNonBlockingConnect()
          .withEndpoint("localhost:7845")
          .withRefresh(1, 10, TimeUnit.MILLISECONDS)
          .withTimeout(3, TimeUnit.SECONDS)
          .build(WalletType.Naive);

      final AergoKey key = new AergoKeyGenerator().create();
      final String password = randomUUID().toString();
      wallet.saveKey(key, password);
      wallet.unlock(Authentication.of(key.getAddress(), password));
      recipient = new AccountFactory().create(key);
    }

    public void send() {
      wallet.send(recipient.getAddress(), Aer.ONE, Fee.getDefaultFee());
    }

    @TearDown(Level.Trial)
    public synchronized void tearDown() throws IOException {
      ((Closeable) wallet).close();
    }
  }

  @Benchmark
  public void send(final User user) {
    user.send();
  }
}


