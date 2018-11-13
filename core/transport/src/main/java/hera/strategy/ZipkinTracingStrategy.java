package hera.strategy;

import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.ExtraFieldPropagation.Factory;
import io.grpc.ManagedChannelBuilder;
import io.opentracing.contrib.ClientTracingInterceptor;
import io.opentracing.util.GlobalTracer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

@RequiredArgsConstructor
public class ZipkinTracingStrategy implements ChannelConfigurationStrategy {

  protected final transient Logger logger = getLogger(getClass());

  @Getter
  protected final String serviceName;

  @Getter
  protected final String zipkinEndpoint;

  protected static boolean isApplied = false;

  public ZipkinTracingStrategy(final String zipkinEndpoint) {
    this("heraj", zipkinEndpoint);
  }

  public ZipkinTracingStrategy() {
    this("http://localhost:9411/api/v2/spans");
  }

  @Override
  public void configure(final ManagedChannelBuilder<?> builder) {
    try {
      if (!GlobalTracer.isRegistered()) {
        final Sender sender = OkHttpSender.create(zipkinEndpoint);
        final AsyncReporter<Span> spanReporter = AsyncReporter.create(sender);

        final Factory propagationFactory = ExtraFieldPropagation
            .newFactoryBuilder(B3Propagation.FACTORY)
            .addPrefixedFields("baggage-", asList("country-code", "user-id"))
            .build();

        final Tracing braveTracing = Tracing.newBuilder()
            .localServiceName(serviceName)
            .propagationFactory(propagationFactory)
            .spanReporter(spanReporter)
            .build();
        final BraveTracer tracer = BraveTracer.create(braveTracing);
        GlobalTracer.register(tracer);
      }

      builder.intercept(new ClientTracingInterceptor(GlobalTracer.get()));
    } catch (final IllegalStateException e) {
      logger.debug("Exception ignored. It may caused by duplicated registration", e);
    }
  }
}
