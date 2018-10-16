/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;

import hera.api.model.Account;
import hera.api.model.Authentication;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.strategy.NettyConnectStrategy;
import java.util.concurrent.atomic.AtomicLong;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@BenchmarkMode(Mode.Throughput)
public class TransactionTemplateBenchmark {

  protected static final String PASSWORD = randomUUID().toString();

  @State(Scope.Thread)
  public static class User {

    protected AergoClient aergoClient;

    protected Account sender;

    protected Account recipient;

    protected AtomicLong nonce = new AtomicLong(1);

    @Setup(Level.Trial)
    public synchronized void setUp() {
      aergoClient =
          new AergoClientBuilder().addStrategy(new NettyConnectStrategy("localhost:7845")).build();
      sender = aergoClient.getAccountOperation().create(PASSWORD);
      recipient = aergoClient.getAccountOperation().create(PASSWORD);
      aergoClient.getAccountOperation().unlock(Authentication.of(sender.getAddress(), PASSWORD));
    }

    @TearDown(Level.Trial)
    public void tearDown() {
      aergoClient.close();
    }

    public void sendTransaction() {
      final Transaction transaction = new Transaction();
      transaction.setNonce(nonce.getAndIncrement());
      transaction.setAmount(30);
      transaction.setSender(sender.getAddress());
      transaction.setRecipient(recipient.getAddress());
      final Signature signature = aergoClient.getSignOperation().sign(null, transaction);

      transaction.setSignature(signature);
      final TxHash txHash = aergoClient.getTransactionOperation().commit(transaction);
      aergoClient.getTransactionOperation().getTransaction(txHash);
    }
  }

  @Benchmark
  public void signAndCommit(final User user) {
    user.sendTransaction();
  }
}


