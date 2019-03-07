/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.EncodingUtils.encodeHexa;
import static hera.util.TransportUtils.copyFrom;
import static hera.util.VersionUtils.envelop;
import static hera.util.VersionUtils.trim;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.spec.EncryptedPrivateKeySpec;
import hera.util.HexUtils;
import org.slf4j.Logger;
import types.Rpc;

public class EncryptedPrivateKeyConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<EncryptedPrivateKey, Rpc.SingleBytes> domainConverter =
      new Function1<EncryptedPrivateKey, Rpc.SingleBytes>() {

        @Override
        public Rpc.SingleBytes apply(final EncryptedPrivateKey domainEncryptedPrivateKey) {
          if (logger.isTraceEnabled()) {
            logger.trace("Domain encrypted privateKey to convert. with checksum: {}, hexa: {}",
                domainEncryptedPrivateKey, encodeHexa(domainEncryptedPrivateKey.getBytesValue()));
          }
          if (domainEncryptedPrivateKey.getBytesValue().isEmpty()) {
            return Rpc.SingleBytes.newBuilder()
                .setValue(copyFrom(domainEncryptedPrivateKey.getBytesValue())).build();
          }
          final BytesValue withVersion = domainEncryptedPrivateKey.getBytesValue();
          final BytesValue withoutVersion = trim(withVersion);
          final Rpc.SingleBytes rpcEncryptedPrivateKey =
              Rpc.SingleBytes.newBuilder().setValue(copyFrom(withoutVersion)).build();
          if (logger.isTraceEnabled()) {
            logger.trace("Rpc encrypted private key convert. hexa: {}",
                HexUtils.encode(rpcEncryptedPrivateKey.getValue().toByteArray()));
          }
          return rpcEncryptedPrivateKey;
        }
      };

  protected final Function1<Rpc.SingleBytes, EncryptedPrivateKey> rpcConverter =
      new Function1<Rpc.SingleBytes, EncryptedPrivateKey>() {

        @Override
        public EncryptedPrivateKey apply(final Rpc.SingleBytes rpcEncryptedPrivateKey) {
          if (logger.isTraceEnabled()) {
            logger.trace("Rpc encrypted privateKey to convert. hexa: {}",
                HexUtils.encode(rpcEncryptedPrivateKey.getValue().toByteArray()));
          }
          if (rpcEncryptedPrivateKey.getValue().isEmpty()) {
            return new EncryptedPrivateKey(BytesValue.EMPTY);
          }
          final byte[] withoutVersion = rpcEncryptedPrivateKey.getValue().toByteArray();
          final byte[] withVersion = envelop(withoutVersion, EncryptedPrivateKeySpec.PREFIX);
          final EncryptedPrivateKey domainEncryptedPrivateKey =
              new EncryptedPrivateKey(of(withVersion));
          if (logger.isTraceEnabled()) {
            logger.trace("Domain encrypted private key converted. with checksum: {}, hexa: {}",
                domainEncryptedPrivateKey, encodeHexa(domainEncryptedPrivateKey.getBytesValue()));
          }
          return domainEncryptedPrivateKey;
        }
      };

  public ModelConverter<EncryptedPrivateKey, Rpc.SingleBytes> create() {
    return new ModelConverter<EncryptedPrivateKey, Rpc.SingleBytes>(domainConverter, rpcConverter);
  }

}
