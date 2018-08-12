/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static types.AergoRPCServiceGrpc.newBlockingStub;

import hera.api.model.Account;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
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

    protected ManagedChannel channel = null;

    protected AccountTemplate accountTemplate = null;

    protected TransactionTemplate transactionTemplate = null;

    protected Account sender;

    protected Account recipient;

    protected AtomicLong nonce = new AtomicLong(1);

    @Setup(Level.Trial)
    public synchronized void setUp() {
      channel = NettyChannelBuilder
          .forAddress("localhost", 7845)
          .usePlaintext()
          .build();
      accountTemplate = new AccountTemplate(channel);
      transactionTemplate = new TransactionTemplate(channel);
      sender = accountTemplate.create(PASSWORD);
      recipient = accountTemplate.create(PASSWORD);
      sender.setPassword(PASSWORD);
      accountTemplate.unlock(sender);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
      channel.shutdown();
    }

    public void sendTransaction() {
      final Transaction transaction = new Transaction();
      transaction.setNonce(nonce.getAndIncrement());
      transaction.setAmount(30);
      transaction.setSender(sender.getAddress());
      transaction.setRecipient(recipient.getAddress());
      final Signature signature = transactionTemplate.sign(transaction);

      transaction.setSignature(signature);
      transactionTemplate.commit(transaction);
      transactionTemplate.getTransaction(signature.getHash());
    }
  }

  @Benchmark
  public void signAndCommit(final User user) {
    user.sendTransaction();
  }
}


