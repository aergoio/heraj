/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import java.io.IOException;
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

    protected int intVal;

    protected String stringVal;

    public int getIntVal() {
      return intVal;
    }

    public void setIntVal(int intVal) {
      this.intVal = intVal;
    }

    public String getStringVal() {
      return stringVal;
    }

    public void setStringVal(String stringVal) {
      this.stringVal = stringVal;
    }

  }

  @State(Scope.Thread)
  public static class User {

    protected BytesValue rawResult;

    @Setup(Level.Trial)
    public synchronized void setUp() {
      final String rawData = "{\"intVal\":-1858492432,\"stringVal\":\"I am string\"}";
      this.rawResult = BytesValue.of(rawData.getBytes());
    }

    public Data bind() throws IOException {
      return ContractResult.of(rawResult).bind(Data.class);
    }

  }

  @Benchmark
  public void bind(final User user) throws IOException {
    user.bind();
  }

}


