/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;

import hera.api.SignLocalEitherTemplate;
import hera.api.model.Transaction;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.concurrent.atomic.AtomicLong;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@BenchmarkMode(Mode.Throughput)
public class SignTemplateBenchmark {

  protected static final String PASSWORD = randomUUID().toString();

  @State(Scope.Thread)
  public static class User {

    protected SignLocalEitherTemplate signTemplate;

    protected AergoKey senderKey;
    protected AergoKey receiptKey;

    protected AtomicLong nonce = new AtomicLong(1);

    @Setup(Level.Trial)
    public synchronized void setUp() throws Exception {
      signTemplate = new SignLocalEitherTemplate(AergoClientBuilder.getDefaultContext());
      senderKey = new AergoKeyGenerator().create();
      receiptKey = new AergoKeyGenerator().create();
    }

    public void sendTransaction() throws Exception {
      final Transaction transaction = new Transaction();
      transaction.setNonce(nonce.getAndIncrement());
      transaction.setAmount(30);
      transaction.setSender(senderKey.getAddress());
      transaction.setRecipient(receiptKey.getAddress());

      signTemplate.sign(senderKey, transaction);
    }
  }

  @Benchmark
  public void sign(final User user) throws Exception {
    user.sendTransaction();
  }
}


