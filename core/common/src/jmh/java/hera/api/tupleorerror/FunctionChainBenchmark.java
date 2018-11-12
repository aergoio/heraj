package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.of;
import static hera.api.tupleorerror.FunctionChain.seq;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

@BenchmarkMode(Mode.Throughput)
public class FunctionChainBenchmark {

  protected int supply() {
    return 3;
  }

  @Benchmark
  public void seq4ParallelAndMap() {
    seq(() -> of(() -> supply()), () -> of(() -> supply()), () -> of(() -> supply()),
        () -> of(() -> supply())).map((a, b, c, d) -> a + b + c + d).map(Object::toString);
  }

}
