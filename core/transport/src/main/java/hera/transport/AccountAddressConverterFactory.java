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

import com.google.protobuf.ByteString;
import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.util.HexUtils;
import org.slf4j.Logger;

public class AccountAddressConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<AccountAddress, com.google.protobuf.ByteString> domainConverter =
      new Function1<AccountAddress, ByteString>() {

        @Override
        public com.google.protobuf.ByteString apply(final AccountAddress domainAccountAddress) {
          if (logger.isTraceEnabled()) {
            logger.trace("Domain account address to convert. with checksum: {}, hexa: {}",
                domainAccountAddress,
                encodeHexa(domainAccountAddress.getBytesValue()));
          }
          ByteString rpcAccountAddress;
          if (false == domainAccountAddress.getBytesValue().isEmpty()) {
            final byte[] withVersion = domainAccountAddress.getBytesValue().getValue();
            final byte[] withoutVersion = trim(withVersion);
            rpcAccountAddress = copyFrom(withoutVersion);
          } else {
            rpcAccountAddress = ByteString.EMPTY;
          }
          if (logger.isTraceEnabled()) {
            logger.trace("Rpc account address converted. hexa: {}",
                HexUtils.encode(rpcAccountAddress.toByteArray()));
          }
          return rpcAccountAddress;
        }
      };

  protected final Function1<com.google.protobuf.ByteString, AccountAddress> rpcConverter =
      new Function1<com.google.protobuf.ByteString, AccountAddress>() {

        @Override
        public AccountAddress apply(com.google.protobuf.ByteString rpcAccountAddress) {
          if (logger.isTraceEnabled()) {
            logger.trace("Rpc account address to convert. hexa: {}",
                HexUtils.encode(rpcAccountAddress.toByteArray()));
          }
          AccountAddress domainAccountAddress;
          if (false == rpcAccountAddress.isEmpty()) {
            final byte[] withoutVersion = rpcAccountAddress.toByteArray();
            final byte[] withVersion = envelop(withoutVersion, AccountAddress.VERSION);
            domainAccountAddress = new AccountAddress(of(withVersion));
          } else {
            domainAccountAddress = new AccountAddress(BytesValue.EMPTY);
          }
          if (logger.isTraceEnabled()) {
            logger.trace("Domain account address converted. with checksum: {}, hexa: {}",
                domainAccountAddress,
                encodeHexa(domainAccountAddress.getBytesValue()));
          }
          return domainAccountAddress;
        }
      };

  public ModelConverter<AccountAddress, com.google.protobuf.ByteString> create() {
    return new ModelConverter<AccountAddress, com.google.protobuf.ByteString>(domainConverter,
        rpcConverter);
  }

}
