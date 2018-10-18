/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.api.model.Account;
import hera.api.model.ClientManagedAccount;
import hera.api.model.Transaction;
import hera.key.AergoKeyGenerator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@BenchmarkMode(Mode.Throughput)
public class SignLocalBenchmark {

  @State(Scope.Thread)
  public static class User {

    protected AergoClient client;
    protected Account sender;
    protected Account receipt;

    @Setup(Level.Trial)
    public synchronized void setUp() throws Exception {
      client = new AergoClientBuilder().build();

      final AergoKeyGenerator generator = new AergoKeyGenerator();
      sender = ClientManagedAccount.of(generator.create());
      receipt = ClientManagedAccount.of(generator.create());
    }

    public void sendTransaction() throws Exception {
      final Transaction transaction = new Transaction();
      transaction.setNonce(sender.getNonceAndImcrement());
      transaction.setAmount(30);
      transaction.setSender(sender);
      transaction.setRecipient(receipt);

      client.getAccountOperation().sign(sender, transaction);
    }
  }

  @Benchmark
  public void sign(final User user) throws Exception {
    user.sendTransaction();
  }
}


