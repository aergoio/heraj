/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.AddressUtils.deriveAddress;
import static hera.util.TransportUtils.sha256AndEncodeHexa;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import org.slf4j.Logger;
import types.AccountOuterClass;
import types.Rpc;

public class AuthenticationConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final Function1<Authentication, Rpc.Personal> domainConverter =
      new Function1<Authentication, Rpc.Personal>() {

        @Override
        public Rpc.Personal apply(final Authentication domainAuthentication) {
          logger.trace("Domain authentication to convert: {}", domainAuthentication);

          final AccountAddress accountAddress = deriveAddress(domainAuthentication.getIdentity());
          final AccountOuterClass.Account rpcAccount = AccountOuterClass.Account.newBuilder()
              .setAddress(accountAddressConverter.convertToRpcModel(accountAddress))
              .build();
          final Rpc.Personal rpcAuthentication = Rpc.Personal.newBuilder()
              .setAccount(rpcAccount)
              .setPassphrase(domainAuthentication.getPassword()).build();
          if (logger.isTraceEnabled()) {
            logger.trace("Rpc authentication converted: Personal(account={}, passphrase={})",
                rpcAuthentication.getAccount(),
                sha256AndEncodeHexa(rpcAuthentication.getPassphrase()));
          }
          return rpcAuthentication;
        }
      };

  protected final Function1<Rpc.Personal, Authentication> rpcConverter =
      new Function1<Rpc.Personal, Authentication>() {

        @Override
        public Authentication apply(final Rpc.Personal rpcAuthentication) {
          throw new UnsupportedOperationException();
        }
      };

  public ModelConverter<Authentication, Rpc.Personal> create() {
    return new ModelConverter<Authentication, Rpc.Personal>(domainConverter, rpcConverter);
  }

}
