/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.api.model.Account;
import hera.api.model.ClientManagedAccount;
import hera.api.model.Signature;
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

    protected Transaction signed;

    @Setup(Level.Trial)
    public synchronized void setUp() {
      client = new AergoClientBuilder().build();

      final AergoKeyGenerator generator = new AergoKeyGenerator();
      sender = ClientManagedAccount.of(generator.create());
      receipt = ClientManagedAccount.of(generator.create());

      signed = new Transaction();
      signed.setNonce(sender.nextNonce());
      signed.setAmount(30);
      signed.setSender(sender);
      signed.setRecipient(receipt);
      client.getAccountOperation().sign(sender, signed);
      final Signature signature = client.getAccountOperation().sign(sender, signed);
      signed.setSignature(signature);
    }

    public void sign() {
      final Transaction transaction = new Transaction();
      transaction.setNonce(sender.nextNonce());
      transaction.setAmount(30);
      transaction.setSender(sender);
      transaction.setRecipient(receipt);

      client.getAccountOperation().sign(sender, transaction);
    }

    public void verify() {
      client.getAccountOperation().verify(sender, signed);
    }

    public void signAndVerify() {
      final Transaction transaction = new Transaction();
      transaction.setNonce(sender.nextNonce());
      transaction.setAmount(30);
      transaction.setSender(sender);
      transaction.setRecipient(receipt);

      final Signature signature = client.getAccountOperation().sign(sender, transaction);
      transaction.setSignature(signature);
      client.getAccountOperation().verify(sender, transaction);
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


