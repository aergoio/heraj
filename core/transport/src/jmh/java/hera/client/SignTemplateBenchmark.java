/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;

import hera.api.SignTemplate;
import hera.api.model.AccountAddress;
import hera.api.model.Hash;
import hera.api.model.Transaction;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSAKeyGenerator;
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
public class SignTemplateBenchmark {

  protected static final String PASSWORD = randomUUID().toString();

  @State(Scope.Thread)
  public static class User {

    protected ManagedChannel channel = null;

    protected SignTemplate signTemplate;

    protected ECDSAKey senderKey;

    protected AtomicLong nonce = new AtomicLong(1);

    @Setup(Level.Trial)
    public synchronized void setUp() throws Exception {
      channel = NettyChannelBuilder
          .forAddress("localhost", 7845)
          .usePlaintext()
          .build();
      signTemplate = new SignTemplate();
      senderKey = new ECDSAKeyGenerator().create();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
      channel.shutdown();
    }

    public void sendTransaction() throws Exception {
      final Transaction transaction = new Transaction();
      transaction.setNonce(nonce.getAndIncrement());
      transaction.setAmount(30);
      transaction.setSender(AccountAddress.of(randomUUID().toString().getBytes()));
      transaction.setRecipient(AccountAddress.of(randomUUID().toString().getBytes()));

      final Hash hashWithoutSign = transaction.calculateHash();
      signTemplate.sign(senderKey, hashWithoutSign);
    }
  }

  @Benchmark
  public void sign(final User user) throws Exception {
    user.sendTransaction();
  }
}


