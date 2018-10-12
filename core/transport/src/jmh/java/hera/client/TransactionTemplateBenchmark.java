/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;

import hera.api.model.Account;
import hera.api.model.Authentication;
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

    protected SignEitherTemplate signTemplate = null;

    protected AccountEitherTemplate accountTemplate = null;

    protected TransactionEitherTemplate transactionTemplate = null;

    protected Account sender;

    protected Account recipient;

    protected AtomicLong nonce = new AtomicLong(1);

    @Setup(Level.Trial)
    public synchronized void setUp() {
      channel = NettyChannelBuilder
          .forAddress("localhost", 7845)
          .usePlaintext()
          .build();
      signTemplate = new SignEitherTemplate(channel, null);
      accountTemplate = new AccountEitherTemplate(channel, null);
      transactionTemplate = new TransactionEitherTemplate(channel, null);
      sender = accountTemplate.create(PASSWORD).getResult();
      recipient = accountTemplate.create(PASSWORD).getResult();
      accountTemplate.unlock(Authentication.of(sender.getAddress(), PASSWORD));
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
      final Signature signature = signTemplate.sign(null, transaction).getResult();

      transaction.setSignature(signature);
      transactionTemplate.commit(transaction);
      transactionTemplate.getTransaction(signature.getTxHash());
    }
  }

  @Benchmark
  public void signAndCommit(final User user) {
    user.sendTransaction();
  }
}


