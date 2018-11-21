package hera.strategy;

import static hera.util.ObjectUtils.nvl;
import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.ExtraFieldPropagation.Factory;
import hera.util.Configurable;
import hera.util.Configuration;
import hera.util.conf.DummyConfiguration;
import hera.util.trace.HttpSender;
import hera.util.trace.KafkaSender;
import io.grpc.ManagedChannelBuilder;
import io.opentracing.contrib.ClientTracingInterceptor;
import io.opentracing.util.GlobalTracer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;

@RequiredArgsConstructor
public class ZipkinTracingStrategy
    implements ChannelConfigurationStrategy, Configurable<Configuration> {

  protected final transient Logger logger = getLogger(getClass());

  protected static final String DEFAULT_ZIPKIN_PROTOCOL = "http";

  protected static final String DEFAULT_ZIPKIN_HTTP_ENDPOINT = "localhost:9411";
  protected static final String DEFAULT_ZIPKIN_KAFKA_ENDPOINT = "localhost:9092";

  protected static final String DEFAULT_ZIPKIN_SERVICE = "heraj";

  @Getter
  @NonNull
  protected Configuration configuration = DummyConfiguration.getInstance();

  public void setConfiguration(final Configuration configuration) {
    this.configuration = nvl(configuration, DummyConfiguration.getInstance());
  }

  protected String getProtocol() {
    return configuration.getAsString("zipkin.protocol", DEFAULT_ZIPKIN_PROTOCOL);
  }

  protected String getEndpoint() {
    final String protocol = getProtocol();
    if ("http".equals(protocol)) {
      return configuration.getAsString("zipkin.endpoint", DEFAULT_ZIPKIN_HTTP_ENDPOINT);
    } else if ("kafka".equals(protocol)) {
      return configuration.getAsString("zipkin.endpoint", DEFAULT_ZIPKIN_KAFKA_ENDPOINT);
    } else {
      throw new IllegalArgumentException("Unknown protocol: " + protocol);
    }
  }

  protected String getServiceName() {
    return configuration.getAsString("zipkin.service", DEFAULT_ZIPKIN_SERVICE);
  }

  protected Sender getSender() {
    final String protocol = getProtocol();
    final String endpoint = getEndpoint();
    if ("http".equals(protocol) || "https".equals(protocol)) {
      return new HttpSender().apply(endpoint);
    } else if ("kafka".equals(protocol)) {
      return new KafkaSender().apply(endpoint);
    } else {
      throw new IllegalArgumentException("Unknown protocol: " + protocol);
    }
  }

  @Override
  public void configure(final ManagedChannelBuilder<?> builder) {
    try {
      if (!GlobalTracer.isRegistered()) {
        final Sender sender = getSender();
        logger.debug("Sender: {}", sender);

        final AsyncReporter<Span> spanReporter = AsyncReporter.create(sender);
        logger.debug("Reporter: {}", spanReporter);

        final Factory propagationFactory = ExtraFieldPropagation
            .newFactoryBuilder(B3Propagation.FACTORY)
            .addPrefixedFields("baggage-", asList("country-code", "user-id"))
            .build();
        logger.debug("Propagation factory: {}", propagationFactory);

        final String serviceName = getServiceName();
        logger.debug("Service name: {}", serviceName);

        final Tracing braveTracing = Tracing.newBuilder()
            .localServiceName(serviceName)
            .propagationFactory(propagationFactory)
            .spanReporter(spanReporter)
            .build();
        final BraveTracer tracer = BraveTracer.create(braveTracing);
        logger.debug("Tracer: {}", tracer);
        GlobalTracer.register(tracer);
      }

      builder.intercept(new ClientTracingInterceptor(GlobalTracer.get()));
    } catch (final IllegalStateException e) {
      logger.debug("Exception ignored. It may caused by duplicated registration", e);
    }
  }
}
