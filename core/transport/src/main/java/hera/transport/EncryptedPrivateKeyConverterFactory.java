/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.util.HexUtils;
import java.util.Arrays;
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
        final byte[] withoutVersion = Arrays.copyOfRange(withVersion, 1, withVersion.length);
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
        final byte[] withVersion = new byte[withoutVersion.length + 1];
        withVersion[0] = EncryptedPrivateKey.PRIVATE_KEY_VERSION;
        System.arraycopy(withoutVersion, 0, withVersion, 1, withoutVersion.length);
        return new EncryptedPrivateKey(of(withVersion));
      };

  public ModelConverter<EncryptedPrivateKey, Rpc.SingleBytes> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
