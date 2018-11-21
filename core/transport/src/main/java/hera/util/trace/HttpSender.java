package hera.util.trace;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.function.Function;
import org.slf4j.Logger;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

public class HttpSender implements Function<String, Sender> {
  protected final transient Logger logger = getLogger(getClass());

  protected final String urlPattern = "http://%s/api/v2/spans";

  @Override
  public Sender apply(final String endpoint) {
    final String url = String.format(urlPattern, endpoint);
    return OkHttpSender.create(url);
  }
}
