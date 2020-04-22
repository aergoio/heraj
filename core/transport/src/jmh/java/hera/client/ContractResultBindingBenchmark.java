/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.api.model.BytesValue;
import hera.api.model.ContractResult;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@BenchmarkMode(Mode.Throughput)
public class ContractResultBindingBenchmark {

  public static class Data {

    @Getter
    @Setter
    protected int intVal;

    @Getter
    @Setter
    protected String stringVal;
  }

  @State(Scope.Thread)
  public static class User {

    protected BytesValue rawResult;

    @Setup(Level.Trial)
    public synchronized void setUp() {
      final String rawData = "{\"intVal\":-1858492432,\"stringVal\":\"I am string\"}";
      this.rawResult = new BytesValue(rawData.getBytes());
    }

    public void bind() throws IOException {
      ContractResult.of(rawResult).bind(Data.class);
    }

  }

  @Benchmark
  public void bind(final User user) throws IOException {
    user.bind();
  }

}


