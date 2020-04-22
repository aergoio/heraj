/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static hera.util.IoUtils.from;
import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.exception.HerajException;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.okhttp.OkHttpChannelBuilder;
import java.io.InputStream;
import javax.net.ssl.SSLSocketFactory;
import lombok.ToString;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
@ToString
public class TlsChannelStrategy implements SecurityConfigurationStrategy {

  @ToString.Exclude
  protected final transient Logger logger = getLogger(getClass());

  @ToString.Include
  protected final String serverName;

  @ToString.Exclude
  protected final BytesValue serverCert;

  @ToString.Exclude
  protected final BytesValue clientCert;

  @ToString.Exclude
  protected final BytesValue clientKey;

  /**
   * TlsChannelStrategy constructor.
   *
   * @param serverName a server common name (CN)
   * @param serverCert a server certification input stream
   * @param clientCert a client certification input stream
   * @param clientKey  a server key input stream
   */
  public TlsChannelStrategy(final String serverName, final InputStream serverCert,
      final InputStream clientCert, final InputStream clientKey) {
    assertNotNull(serverName, "Server common name must not null");
    assertNotNull(serverCert, "Server cert input stream must not null");
    assertNotNull(clientCert, "Client cert input stream must not null");
    assertNotNull(clientKey, "Client key input stream must not null");
    try {
      this.serverName = serverName;
      this.serverCert = BytesValue.of(from(serverCert));
      this.clientCert = BytesValue.of(from(clientCert));
      this.clientKey = BytesValue.of(from(clientKey));
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public void configure(final ManagedChannelBuilder<?> builder) {
    logger.info("Configure channel with tls (server name: {})", serverName);
    try {
      if (builder instanceof NettyChannelBuilder) {
        final SslContext sslContext = GrpcSslContexts.forClient()
            .trustManager(serverCert.getInputStream())
            .keyManager(clientCert.getInputStream(), clientKey.getInputStream())
            .build();
        ((NettyChannelBuilder) builder).sslContext(sslContext);
      } else if (builder instanceof OkHttpChannelBuilder) {
        // TODO : not yet implemented
        final SSLSocketFactory sslSocketFactory = null;
        ((OkHttpChannelBuilder) builder).sslSocketFactory(sslSocketFactory);
      } else {
        throw new HerajException("Unsupported channel builder type " + builder.getClass());
      }
      builder.overrideAuthority(serverName).useTransportSecurity();
    } catch (final HerajException e) {
      throw e;
    } catch (final Exception e) {
      throw new HerajException(e);
    }
  }

}
