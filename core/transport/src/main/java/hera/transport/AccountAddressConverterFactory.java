/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.EncodingUtils.encodeHexa;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.Name;
import hera.api.model.internal.AccountAddressAdaptor;
import hera.spec.resolver.AddressResolver;
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
          if (!domainAccountAddress.equals(AccountAddress.EMPTY)) {
            rpcAccountAddress = copyFrom(domainAccountAddress.getBytesValue());
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
          if (!rpcAccountAddress.equals(ByteString.EMPTY)) {
            final BytesValue rawAddress = BytesValue.of(rpcAccountAddress.toByteArray());
            if (AddressResolver.isValidRawAddress(rawAddress)) {
            domainAccountAddress = new AccountAddress(rawAddress);
            } else {
            // FIXME : treat as name. no other way?
            final Name name = Name.of(new String(rawAddress.getValue()));
            domainAccountAddress = new AccountAddressAdaptor(name);
            }
          } else {
            domainAccountAddress = AccountAddress.EMPTY;
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
