/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.ListenableFuture;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function0;
import hera.api.function.Function1;
import hera.api.function.Function3;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.transport.AccountConverterFactory;
import hera.transport.AuthenticationConverterFactory;
import hera.transport.EncryptedPrivateKeyConverterFactory;
import hera.transport.ModelConverter;
import io.grpc.ManagedChannel;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import types.AccountOuterClass;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
public class KeyStoreBaseTemplate implements ChannelInjectable, ContextProviderInjectable {

  protected final Logger logger = getLogger(getClass());

  protected final ModelConverter<EncryptedPrivateKey, Rpc.SingleBytes> encryptedPkConverter =
      new EncryptedPrivateKeyConverterFactory().create();

  protected final ModelConverter<Account, AccountOuterClass.Account> accountConverter =
      new AccountConverterFactory().create();

  protected final ModelConverter<Authentication, Rpc.Personal> authenticationConverter =
      new AuthenticationConverterFactory().create();

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  protected ContextProvider contextProvider;

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Getter
  private final Function0<FinishableFuture<List<AccountAddress>>> listFunction =
      new Function0<FinishableFuture<List<AccountAddress>>>() {

        @Override
        public FinishableFuture<List<AccountAddress>> apply() {
          if (logger.isDebugEnabled()) {
            logger.debug("List keystore address, Context: {}", contextProvider.get());
          }

          FinishableFuture<List<AccountAddress>> nextFuture =
              new FinishableFuture<List<AccountAddress>>();
          try {
            ListenableFuture<AccountOuterClass.AccountList> listenableFuture =
                aergoService.getAccounts(Rpc.Empty.newBuilder().build());

            FutureChain<AccountOuterClass.AccountList, List<AccountAddress>> callback =
                new FutureChain<AccountOuterClass.AccountList, List<AccountAddress>>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(
                new Function1<AccountOuterClass.AccountList, List<AccountAddress>>() {

                  @Override
                  public List<AccountAddress> apply(
                      final AccountOuterClass.AccountList rpcAccountList) {
                    final List<AccountAddress> domainAccountList =
                        new ArrayList<AccountAddress>();
                    for (final AccountOuterClass.Account rpcAccount : rpcAccountList
                        .getAccountsList()) {
                      domainAccountList
                          .add(accountConverter.convertToDomainModel(rpcAccount).getAddress());
                    }
                    return domainAccountList;
                  }
                });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function1<String, FinishableFuture<Account>> createFunction =
      new Function1<String, FinishableFuture<Account>>() {

        @Override
        public FinishableFuture<Account> apply(final String password) {
          if (logger.isDebugEnabled()) {
            logger.debug("Create an account to server keystore, Context: {}",
                contextProvider.get());
          }

          FinishableFuture<Account> nextFuture = new FinishableFuture<Account>();
          try {
            Rpc.Personal personal = Rpc.Personal.newBuilder().setPassphrase(password).build();
            ListenableFuture<AccountOuterClass.Account> listenableFuture =
                aergoService.createAccount(personal);

            FutureChain<AccountOuterClass.Account, Account> callback =
                new FutureChain<AccountOuterClass.Account, Account>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<AccountOuterClass.Account, Account>() {

              @Override
              public Account apply(final AccountOuterClass.Account rpcAccount) {
                return accountConverter.convertToDomainModel(rpcAccount);
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function1<Authentication, FinishableFuture<Boolean>> unlockFunction =
      new Function1<Authentication, FinishableFuture<Boolean>>() {

        @Override
        public FinishableFuture<Boolean> apply(final Authentication authentication) {
          if (logger.isDebugEnabled()) {
            logger.debug("Unlock an account in server keystore, Address: {}, Context: {}",
                authentication.getAddress(), contextProvider.get());
          }

          FinishableFuture<Boolean> nextFuture = new FinishableFuture<Boolean>();
          try {
            final Rpc.Personal request = authenticationConverter.convertToRpcModel(authentication);
            ListenableFuture<AccountOuterClass.Account> listenableFuture =
                aergoService.unlockAccount(request);

            FutureChain<AccountOuterClass.Account, Boolean> callback =
                new FutureChain<AccountOuterClass.Account, Boolean>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<AccountOuterClass.Account, Boolean>() {

              @Override
              public Boolean apply(final AccountOuterClass.Account rpcAccount) {
                return null != rpcAccount.getAddress();
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function1<Authentication, FinishableFuture<Boolean>> lockFunction =
      new Function1<Authentication, FinishableFuture<Boolean>>() {

        @Override
        public FinishableFuture<Boolean> apply(final Authentication authentication) {
          if (logger.isDebugEnabled()) {
            logger.debug("Lock an account in server keystore, Address: {}, Context: {}",
                authentication.getAddress(), contextProvider.get());
          }

          FinishableFuture<Boolean> nextFuture = new FinishableFuture<Boolean>();
          try {
            ListenableFuture<AccountOuterClass.Account> listenableFuture =
                aergoService.lockAccount(authenticationConverter.convertToRpcModel(authentication));

            FutureChain<AccountOuterClass.Account, Boolean> callback =
                new FutureChain<AccountOuterClass.Account, Boolean>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<AccountOuterClass.Account, Boolean>() {

              @Override
              public Boolean apply(final AccountOuterClass.Account rpcAccount) {
                return null != rpcAccount.getAddress();
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function3<EncryptedPrivateKey, String, String,
      FinishableFuture<Account>> importKeyFunction = new Function3<
          EncryptedPrivateKey, String, String, FinishableFuture<Account>>() {

        @Override
        public FinishableFuture<Account> apply(final EncryptedPrivateKey encryptedKey,
            final String oldPassword, final String newPassword) {
          if (logger.isDebugEnabled()) {
            logger.debug(
                "Import an account to server keystore, EncryptedKey: {}, Context: {}",
                encryptedKey, contextProvider.get());
          }

          FinishableFuture<Account> nextFuture = new FinishableFuture<Account>();
          try {
            final Rpc.ImportFormat request = Rpc.ImportFormat.newBuilder()
                .setWif(encryptedPkConverter.convertToRpcModel(encryptedKey))
                .setOldpass(oldPassword).setNewpass(newPassword).build();
            ListenableFuture<AccountOuterClass.Account> listenableFuture =
                aergoService.importAccount(request);

            FutureChain<AccountOuterClass.Account, Account> callback =
                new FutureChain<AccountOuterClass.Account, Account>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<AccountOuterClass.Account, Account>() {

              @Override
              public Account apply(final AccountOuterClass.Account rpcAccount) {
                return accountConverter.convertToDomainModel(rpcAccount);
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function1<Authentication,
      FinishableFuture<EncryptedPrivateKey>> exportKeyFunction = new Function1<
          Authentication, FinishableFuture<EncryptedPrivateKey>>() {

        @Override
        public FinishableFuture<EncryptedPrivateKey> apply(
            final Authentication authentication) {
          if (logger.isDebugEnabled()) {
            logger.debug("Export an account from server keystore, Address: {}, Context: {}",
                authentication.getAddress(), contextProvider.get());
          }

          FinishableFuture<EncryptedPrivateKey> nextFuture =
              new FinishableFuture<EncryptedPrivateKey>();
          try {
            final Rpc.Personal request =
                authenticationConverter.convertToRpcModel(authentication);
            ListenableFuture<Rpc.SingleBytes> listenableFuture =
                aergoService.exportAccount(request);

            FutureChain<Rpc.SingleBytes, EncryptedPrivateKey> callback =
                new FutureChain<Rpc.SingleBytes, EncryptedPrivateKey>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<Rpc.SingleBytes, EncryptedPrivateKey>() {

              @Override
              public EncryptedPrivateKey apply(final Rpc.SingleBytes rawPk) {
                return encryptedPkConverter.convertToDomainModel(rawPk);
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

}
