/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.VersionUtils;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.util.HexUtils;
import java.util.function.Function;
import org.slf4j.Logger;

public class AccountAddressConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function<AccountAddress, com.google.protobuf.ByteString> domainConverter =
      domainAccountAddress -> {
        logger.trace("Domain account address: {}",
            HexUtils.encode(domainAccountAddress.getBytesValue().getValue()));
        if (domainAccountAddress.getBytesValue().isEmpty()) {
          return copyFrom(domainAccountAddress.getBytesValue());
        }
        final byte[] withVersion = domainAccountAddress.getBytesValue().getValue();
        final byte[] withoutVersion = VersionUtils.trim(withVersion);
        return ByteString.copyFrom(withoutVersion);
      };

  protected final Function<com.google.protobuf.ByteString, AccountAddress> rpcConverter =
      rpcAccountAddress -> {
        logger.trace("Rpc account address: {}", HexUtils.encode(rpcAccountAddress.toByteArray()));
        if (rpcAccountAddress.isEmpty()) {
          return new AccountAddress(BytesValue.EMPTY);
        }
        final byte[] withoutVersion = rpcAccountAddress.toByteArray();
        final byte[] withVersion = VersionUtils.envelop(withoutVersion, AccountAddress.VERSION);
        return new AccountAddress(of(withVersion));
      };

  public ModelConverter<AccountAddress, com.google.protobuf.ByteString> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
