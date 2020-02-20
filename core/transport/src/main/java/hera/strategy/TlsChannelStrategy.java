/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.exception.RpcException;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.okhttp.OkHttpChannelBuilder;
import java.io.InputStream;
import javax.net.ssl.SSLSocketFactory;
import lombok.ToString;
import org.slf4j.Logger;

@ToString(exclude = "logger")
public class TlsChannelStrategy implements SecurityConfigurationStrategy {

  protected final Logger logger = getLogger(getClass());

  protected final String serverCommonName;

  protected final InputStream serverCertInputStream;

  protected final InputStream clientCertInputStream;

  protected final InputStream clientKeyInputStream;

  /**
   * TlsChannelStrategy constructor.
   *
   * @param serverCommonName a server common name (CN)
   * @param serverCertInputStream a server certification input stream
   * @param clientCertInputStream a client certification input stream
   * @param clientKeyInputStream a server key input stream
   */
  public TlsChannelStrategy(final String serverCommonName, final InputStream serverCertInputStream,
      final InputStream clientCertInputStream, final InputStream clientKeyInputStream) {
    assertNotNull(serverCommonName, "Server common name must not null");
    assertNotNull(serverCertInputStream, "Server cert input stream must not null");
    assertNotNull(clientCertInputStream, "Client cert input stream must not null");
    assertNotNull(clientKeyInputStream, "Client key input stream must not null");
    this.serverCommonName = serverCommonName;
    this.serverCertInputStream = serverCertInputStream;
    this.clientCertInputStream = clientCertInputStream;
    this.clientKeyInputStream = clientKeyInputStream;
  }

  @Override
  public void configure(final ManagedChannelBuilder<?> builder) {
    logger.info("Configure channel with tls (server name: {})", serverCommonName);
    try {
      if (builder instanceof NettyChannelBuilder) {
        final SslContext sslContext = GrpcSslContexts.forClient()
            .trustManager(serverCertInputStream)
            .keyManager(clientCertInputStream, clientKeyInputStream)
            .build();
        ((NettyChannelBuilder) builder).sslContext(sslContext);
      } else if (builder instanceof OkHttpChannelBuilder) {
        // TODO : not yet implemented
        final SSLSocketFactory sslSocketFactory = null;
        ((OkHttpChannelBuilder) builder).sslSocketFactory(sslSocketFactory);
      } else {
        throw new RpcException("Unsupported channel builder type " + builder.getClass());
      }
      builder.overrideAuthority(serverCommonName).useTransportSecurity();
    } catch (final RpcException e) {
      throw e;
    } catch (final Exception e) {
      throw new RpcException(e);
    }
  }

  @Override
  public boolean equals(final Object obj) {
    return (null != obj) && (obj instanceof SecurityConfigurationStrategy);
  }

  @Override
  public int hashCode() {
    return TlsChannelStrategy.class.hashCode();
  }

}
