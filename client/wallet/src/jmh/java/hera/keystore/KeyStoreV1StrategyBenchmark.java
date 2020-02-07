/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static java.util.UUID.randomUUID;

import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@BenchmarkMode(Mode.Throughput)
public class KeyStoreV1StrategyBenchmark {

  @State(Scope.Thread)
  public static class User {

    protected KeyFormatStrategy keyStoreStrategy;
    protected AergoKey key;
    protected char[] password;
    protected String json;

    @Setup(Level.Trial)
    public synchronized void setUp() {
      final KeyFormatStrategy keyStoreStrategy = new KeyFormatStrategyFactory().create("v1");

      this.keyStoreStrategy = keyStoreStrategy;
      this.key = new AergoKeyGenerator().create();
      this.password = randomUUID().toString().toCharArray();
      this.json = keyStoreStrategy.encrypt(key, password);
    }

    public void encrypt() {
      this.keyStoreStrategy.encrypt(key, password);
    }

    public void decrypt() {
      this.keyStoreStrategy.decrypt(json, password);
    }

  }

  @Benchmark
  public void encrypt(final User user) {
    user.encrypt();
  }

  @Benchmark
  public void decrypt(final User user) {
    user.decrypt();
  }

}
