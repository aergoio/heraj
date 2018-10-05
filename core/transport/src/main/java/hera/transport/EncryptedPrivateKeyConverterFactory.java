/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.VersionUtils;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.util.HexUtils;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Rpc;

public class EncryptedPrivateKeyConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function<EncryptedPrivateKey, Rpc.SingleBytes> domainConverter =
      domainEncryptedPrivateKey -> {
        logger.trace("Domain encrypted privateKey: {}",
            HexUtils.encode(domainEncryptedPrivateKey.getBytesValue().getValue()));
        if (domainEncryptedPrivateKey.getBytesValue().isEmpty()) {
          return Rpc.SingleBytes.newBuilder()
              .setValue(copyFrom(domainEncryptedPrivateKey.getBytesValue())).build();
        }
        final byte[] withVersion = domainEncryptedPrivateKey.getBytesValue().getValue();
        final byte[] withoutVersion = VersionUtils.trim(withVersion);
        return Rpc.SingleBytes.newBuilder().setValue(ByteString.copyFrom(withoutVersion)).build();
      };

  protected final Function<Rpc.SingleBytes, EncryptedPrivateKey> rpcConverter =
      rpcEncryptedPrivateKey -> {
        logger.trace("Rpc encrypted privateKey: {}",
            HexUtils.encode(rpcEncryptedPrivateKey.getValue().toByteArray()));
        if (rpcEncryptedPrivateKey.getValue().isEmpty()) {
          return new EncryptedPrivateKey(BytesValue.EMPTY);
        }
        final byte[] withoutVersion = rpcEncryptedPrivateKey.getValue().toByteArray();
        final byte[] withVersion =
            VersionUtils.envelop(withoutVersion, EncryptedPrivateKey.VERSION);
        return new EncryptedPrivateKey(of(withVersion));
      };

  public ModelConverter<EncryptedPrivateKey, Rpc.SingleBytes> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
