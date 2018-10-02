/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.util.HexUtils;
import java.util.Arrays;
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
        return ByteString.copyFrom(Arrays.copyOfRange(withVersion, 1, withVersion.length));
      };

  protected final Function<com.google.protobuf.ByteString, AccountAddress> rpcConverter =
      rpcAccountAddress -> {
        logger.trace("Rpc account address: {}", HexUtils.encode(rpcAccountAddress.toByteArray()));
        if (rpcAccountAddress.isEmpty()) {
          return new AccountAddress(BytesValue.EMPTY);
        }
        final byte[] withoutVersion = rpcAccountAddress.toByteArray();
        final byte[] withVersion = new byte[withoutVersion.length + 1];
        withVersion[0] = AccountAddress.ADDRESS_VERSION;
        System.arraycopy(withoutVersion, 0, withVersion, 1, withoutVersion.length);
        return new AccountAddress(of(withVersion));
      };

  public ModelConverter<AccountAddress, com.google.protobuf.ByteString> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
