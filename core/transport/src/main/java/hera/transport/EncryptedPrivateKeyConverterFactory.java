/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.function.Function1;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.util.HexUtils;
import hera.util.VersionUtils;
import org.slf4j.Logger;
import types.Rpc;

public class EncryptedPrivateKeyConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<EncryptedPrivateKey, Rpc.SingleBytes> domainConverter =
      new Function1<EncryptedPrivateKey, Rpc.SingleBytes>() {

        @Override
        public Rpc.SingleBytes apply(final EncryptedPrivateKey domainEncryptedPrivateKey) {
          logger.trace("Domain encrypted privateKey: {}",
              HexUtils.encode(domainEncryptedPrivateKey.getBytesValue().getValue()));
          if (domainEncryptedPrivateKey.getBytesValue().isEmpty()) {
            return Rpc.SingleBytes.newBuilder()
                .setValue(copyFrom(domainEncryptedPrivateKey.getBytesValue())).build();
          }
          final byte[] withVersion = domainEncryptedPrivateKey.getBytesValue().getValue();
          final byte[] withoutVersion = VersionUtils.trim(withVersion);
          return Rpc.SingleBytes.newBuilder().setValue(ByteString.copyFrom(withoutVersion)).build();
        }
      };

  protected final Function1<Rpc.SingleBytes, EncryptedPrivateKey> rpcConverter =
      new Function1<Rpc.SingleBytes, EncryptedPrivateKey>() {

        @Override
        public EncryptedPrivateKey apply(final Rpc.SingleBytes rpcEncryptedPrivateKey) {
          logger.trace("Rpc encrypted privateKey: {}",
              HexUtils.encode(rpcEncryptedPrivateKey.getValue().toByteArray()));
          if (rpcEncryptedPrivateKey.getValue().isEmpty()) {
            return new EncryptedPrivateKey(BytesValue.EMPTY);
          }
          final byte[] withoutVersion = rpcEncryptedPrivateKey.getValue().toByteArray();
          final byte[] withVersion =
              VersionUtils.envelop(withoutVersion, EncryptedPrivateKey.VERSION);
          return new EncryptedPrivateKey(of(withVersion));
        }
      };

  public ModelConverter<EncryptedPrivateKey, Rpc.SingleBytes> create() {
    return new ModelConverter<EncryptedPrivateKey, Rpc.SingleBytes>(domainConverter, rpcConverter);
  }

}
