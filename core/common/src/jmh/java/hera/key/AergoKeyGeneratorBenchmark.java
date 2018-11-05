package hera.key;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@BenchmarkMode(Mode.Throughput)
public class AergoKeyGeneratorBenchmark {

  @State(Scope.Benchmark)
  public static class BenchmarkContext {
    protected AergoKeyGenerator generator = new AergoKeyGenerator();
  }

  @Benchmark
  public void sign(BenchmarkContext context) {
    context.generator.create();
  }

}
