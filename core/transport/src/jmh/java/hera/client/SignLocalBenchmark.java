/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.api.model.Account;
import hera.api.model.AccountFactory;
import hera.api.model.Aer.Unit;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.key.AergoKeyGenerator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@BenchmarkMode(Mode.Throughput)
public class SignLocalBenchmark {

  @State(Scope.Thread)
  public static class User {

    protected AergoClient client;
    protected Account sender;
    protected Account receipt;

    protected RawTransaction raw;
    protected Transaction signed;

    @Setup(Level.Trial)
    public synchronized void setUp() {
      client = new AergoClientBuilder().build();

      final AergoKeyGenerator generator = new AergoKeyGenerator();
      sender = new AccountFactory().create(generator.create());
      receipt = new AccountFactory().create(generator.create());

      raw = Transaction.newBuilder()
          .from(sender)
          .to(receipt)
          .amount("30", Unit.AER)
          .nonce(sender.incrementAndGetNonce())
          .build();
      signed = client.getAccountOperation().sign(sender, raw);
    }

    public void sign() {
      client.getAccountOperation().sign(sender, raw);
    }

    public void verify() {
      client.getAccountOperation().verify(sender, signed);
    }

    public void signAndVerify() {
      final Transaction signedTransaction = client.getAccountOperation().sign(sender, raw);
      client.getAccountOperation().verify(sender, signedTransaction);
    }

    @TearDown(Level.Trial)
    public synchronized void tearDown() {
      client.close();
    }
  }

  @Benchmark
  public void sign(final User user) {
    user.sign();
  }

  @Benchmark
  public void verify(final User user) {
    user.verify();
  }

  @Benchmark
  public void signAndVerify(final User user) {
    user.signAndVerify();
  }

}


