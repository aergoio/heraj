package hera.util.trace;

import static org.slf4j.LoggerFactory.getLogger;
import static zipkin2.reporter.kafka11.KafkaSender.newBuilder;

import java.util.function.Function;
import org.slf4j.Logger;
import zipkin2.codec.Encoding;
import zipkin2.reporter.Sender;

public class KafkaSender implements Function<String, Sender> {
  protected final transient Logger logger = getLogger(getClass());

  @Override
  public Sender apply(final String endpoint) {
    return newBuilder()
        .encoding(Encoding.PROTO3)
        .bootstrapServers("localhost:9092")
        .build();
  }
}
