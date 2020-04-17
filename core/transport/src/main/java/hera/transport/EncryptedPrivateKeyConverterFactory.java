/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.BytesValueUtils.append;
import static hera.util.BytesValueUtils.trimPrefix;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.encode.Encoder;
import hera.api.function.Function1;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
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
                domainEncryptedPrivateKey,
                domainEncryptedPrivateKey.getBytesValue().getEncoded(Encoder.Hex));
          }
          if (domainEncryptedPrivateKey.getBytesValue().isEmpty()) {
            return Rpc.SingleBytes.newBuilder()
                .setValue(copyFrom(domainEncryptedPrivateKey.getBytesValue())).build();
          }
          final BytesValue withVersion = domainEncryptedPrivateKey.getBytesValue();
          final BytesValue withoutVersion = trimPrefix(withVersion);
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
            return EncryptedPrivateKey.EMPTY;
          }
          final byte[] withoutVersion = rpcEncryptedPrivateKey.getValue().toByteArray();
          final byte[] withVersion = append(withoutVersion, EncryptedPrivateKey.ENCRYPTED_PREFIX);
          final EncryptedPrivateKey domainEncryptedPrivateKey =
              new EncryptedPrivateKey(of(withVersion));
          if (logger.isTraceEnabled()) {
            logger.trace("Domain encrypted private key converted. with checksum: {}, hexa: {}",
                domainEncryptedPrivateKey,
                domainEncryptedPrivateKey.getBytesValue().getEncoded(Encoder.Hex));
          }
          return domainEncryptedPrivateKey;
        }
      };

  public ModelConverter<EncryptedPrivateKey, Rpc.SingleBytes> create() {
    return new ModelConverter<EncryptedPrivateKey, Rpc.SingleBytes>(domainConverter, rpcConverter);
  }

}
