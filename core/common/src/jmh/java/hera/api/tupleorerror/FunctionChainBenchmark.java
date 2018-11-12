package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.of;
import static hera.api.tupleorerror.FunctionChain.seq;

import hera.util.ThreadUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

@BenchmarkMode(Mode.Throughput)
public class FunctionChainBenchmark {

  protected int supply() {
//    ThreadUtils.trySleep(1000L);
    return 3;
  }

  @Benchmark
  public void seq4ParallelAndMap() {
    seq(() -> of(() -> supply()), () -> of(() -> supply()), () -> of(() -> supply()),
        () -> of(() -> supply())).map((a, b, c, d) -> a + b + c + d).map(Object::toString);
  }

}
