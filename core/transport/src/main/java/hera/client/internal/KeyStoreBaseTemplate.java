/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.internal;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.util.TransportUtils.sha256AndEncodeHexa;
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
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.client.ChannelInjectable;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.AuthenticationConverterFactory;
import hera.transport.EncryptedPrivateKeyConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.TransactionConverterFactory;
import io.grpc.ManagedChannel;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import types.AccountOuterClass;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
public class KeyStoreBaseTemplate implements ChannelInjectable, ContextProviderInjectable {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<EncryptedPrivateKey, Rpc.SingleBytes> encryptedPkConverter =
      new EncryptedPrivateKeyConverterFactory().create();

  protected final ModelConverter<AccountAddress,
      com.google.protobuf.ByteString> accountAddressConverter =
          new AccountAddressConverterFactory().create();

  protected final ModelConverter<Authentication, Rpc.Personal> authenticationConverter =
      new AuthenticationConverterFactory().create();

  protected final ModelConverter<Transaction, Blockchain.Tx> transactionConverter =
      new TransactionConverterFactory().create();

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
          logger.debug("List keystore addresses");

          FinishableFuture<List<AccountAddress>> nextFuture =
              new FinishableFuture<List<AccountAddress>>();
          try {
            final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
            logger.trace("AergoService getAccounts arg: {}", empty);

            ListenableFuture<AccountOuterClass.AccountList> listenableFuture =
                aergoService.getAccounts(empty);
            FutureChain<AccountOuterClass.AccountList, List<AccountAddress>> callback =
                new FutureChain<AccountOuterClass.AccountList, List<AccountAddress>>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(
                new Function1<AccountOuterClass.AccountList, List<AccountAddress>>() {

                  @Override
                  public List<AccountAddress> apply(
                      final AccountOuterClass.AccountList rpcAccountList) {
                    final List<AccountAddress> domainAccountList = new ArrayList<AccountAddress>();
                    for (final AccountOuterClass.Account rpcAccount : rpcAccountList
                        .getAccountsList()) {
                      domainAccountList.add(
                          accountAddressConverter.convertToDomainModel(rpcAccount.getAddress()));
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
  private final Function1<String, FinishableFuture<AccountAddress>> createFunction =
      new Function1<String, FinishableFuture<AccountAddress>>() {

        @Override
        public FinishableFuture<AccountAddress> apply(final String password) {
          if (logger.isDebugEnabled()) {
            logger.debug("Create an account to server keystore with password: {}",
                sha256AndEncodeHexa(password));
          }

          FinishableFuture<AccountAddress> nextFuture = new FinishableFuture<AccountAddress>();
          try {
            final Rpc.Personal rpcPassword = Rpc.Personal.newBuilder()
                .setPassphrase(password)
                .build();
            if (logger.isTraceEnabled()) {
              logger.trace("AergoService createAccount arg: {}",
                  sha256AndEncodeHexa(rpcPassword.getPassphrase()));
            }

            ListenableFuture<AccountOuterClass.Account> listenableFuture =
                aergoService.createAccount(rpcPassword);
            FutureChain<AccountOuterClass.Account, AccountAddress> callback =
                new FutureChain<AccountOuterClass.Account, AccountAddress>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<AccountOuterClass.Account, AccountAddress>() {

              @Override
              public AccountAddress apply(final AccountOuterClass.Account rpcAccount) {
                return accountAddressConverter.convertToDomainModel(rpcAccount.getAddress());
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
            logger.debug(
                "Unlock an account in server keystore with identification: {}, password: {}",
                authentication.getIdentity(),
                sha256AndEncodeHexa(authentication.getPassword()));
          }

          FinishableFuture<Boolean> nextFuture = new FinishableFuture<Boolean>();
          try {
            final Rpc.Personal rpcAuthentication =
                authenticationConverter.convertToRpcModel(authentication);
            if (logger.isTraceEnabled()) {
              logger.trace("AergoService unlockAccount arg: {}",
                  sha256AndEncodeHexa(rpcAuthentication.getPassphrase()));
            }

            ListenableFuture<AccountOuterClass.Account> listenableFuture =
                aergoService.unlockAccount(rpcAuthentication);
            FutureChain<AccountOuterClass.Account, Boolean> callback =
                new FutureChain<AccountOuterClass.Account, Boolean>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<AccountOuterClass.Account, Boolean>() {

              @Override
              public Boolean apply(final AccountOuterClass.Account rpcAccount) {
                return null != rpcAccount.getAddress();
              }
            });
            callback.setFailureHandler(new Function1<Throwable, Boolean>() {

              @Override
              public Boolean apply(Throwable t) {
                return false;
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            logger.debug("Unlock failure by {}", e.toString());
            nextFuture.success(false);
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
            logger.debug("Lock an account in server keystore with identification: {}, password: {}",
                authentication.getIdentity(),
                sha256AndEncodeHexa(authentication.getPassword()));
          }

          FinishableFuture<Boolean> nextFuture = new FinishableFuture<Boolean>();
          try {
            final Rpc.Personal rpcAuthentication =
                authenticationConverter.convertToRpcModel(authentication);
            if (logger.isTraceEnabled()) {
              logger.trace("AergoService lockAccount arg: {}",
                  sha256AndEncodeHexa(rpcAuthentication.getPassphrase()));
            }

            ListenableFuture<AccountOuterClass.Account> listenableFuture =
                aergoService.lockAccount(rpcAuthentication);
            FutureChain<AccountOuterClass.Account, Boolean> callback =
                new FutureChain<AccountOuterClass.Account, Boolean>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<AccountOuterClass.Account, Boolean>() {

              @Override
              public Boolean apply(final AccountOuterClass.Account rpcAccount) {
                return null != rpcAccount.getAddress();
              }
            });
            callback.setFailureHandler(new Function1<Throwable, Boolean>() {

              @Override
              public Boolean apply(Throwable t) {
                return false;
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            logger.debug("Lock failure by {}", e.toString());
            nextFuture.success(false);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function1<RawTransaction, FinishableFuture<Transaction>> signFunction =
      new Function1<RawTransaction, FinishableFuture<Transaction>>() {

        @Override
        public FinishableFuture<Transaction> apply(final RawTransaction rawTransaction) {
          logger.debug("Sign request with rawTx: {}", rawTransaction);

          FinishableFuture<Transaction> nextFuture = new FinishableFuture<Transaction>();
          final Transaction domainTransaction =
              new Transaction(rawTransaction, null, null, null, 0, false);
          final Blockchain.Tx rpcTx = transactionConverter.convertToRpcModel(domainTransaction);
          logger.trace("AergoService signTX arg: {}", rpcTx);

          ListenableFuture<Blockchain.Tx> listenableFuture = aergoService.signTX(rpcTx);
          FutureChain<Blockchain.Tx, Transaction> callback =
              new FutureChain<Blockchain.Tx, Transaction>(nextFuture, contextProvider.get());
          callback.setSuccessHandler(new Function1<Blockchain.Tx, Transaction>() {
            @Override
            public Transaction apply(final Blockchain.Tx tx) {
              return transactionConverter.convertToDomainModel(tx);
            }
          });
          addCallback(listenableFuture, callback, directExecutor());

          return nextFuture;
        }
      };

  @Getter
  private final Function3<EncryptedPrivateKey, String, String,
      FinishableFuture<AccountAddress>> importKeyFunction = new Function3<
          EncryptedPrivateKey, String, String, FinishableFuture<AccountAddress>>() {

        @Override
        public FinishableFuture<AccountAddress> apply(final EncryptedPrivateKey encryptedKey,
            final String oldPassword, final String newPassword) {
          if (logger.isDebugEnabled()) {
            logger.debug("Import an account to server keystore with "
                + "encryptedKey: {}, oldPassword: {}, newPassword: {}",
                encryptedKey, sha256AndEncodeHexa(oldPassword),
                sha256AndEncodeHexa(newPassword));
          }

          FinishableFuture<AccountAddress> nextFuture = new FinishableFuture<AccountAddress>();
          try {
            final Rpc.ImportFormat rpcImport = Rpc.ImportFormat.newBuilder()
                .setWif(encryptedPkConverter.convertToRpcModel(encryptedKey))
                .setOldpass(oldPassword)
                .setNewpass(newPassword)
                .build();
            if (logger.isTraceEnabled()) {
              logger.trace(
                  "AergoService importAccount arg: ImportFormat(wif={}, oldPass={}, newPass={})",
                  rpcImport.getWif(),
                  sha256AndEncodeHexa(rpcImport.getOldpass()),
                  sha256AndEncodeHexa(rpcImport.getNewpass()));
            }

            ListenableFuture<AccountOuterClass.Account> listenableFuture =
                aergoService.importAccount(rpcImport);
            FutureChain<AccountOuterClass.Account, AccountAddress> callback =
                new FutureChain<AccountOuterClass.Account, AccountAddress>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<AccountOuterClass.Account, AccountAddress>() {

              @Override
              public AccountAddress apply(final AccountOuterClass.Account rpcAccount) {
                return accountAddressConverter.convertToDomainModel(rpcAccount.getAddress());
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
            logger.debug(
                "Export an account from server keystore with identification: {}, password: {}",
                authentication.getIdentity(),
                sha256AndEncodeHexa(authentication.getPassword()));
          }

          FinishableFuture<EncryptedPrivateKey> nextFuture =
              new FinishableFuture<EncryptedPrivateKey>();
          try {
            final Rpc.Personal rpcAuthentication =
                authenticationConverter.convertToRpcModel(authentication);
            if (logger.isTraceEnabled()) {
              logger.trace("AergoService exportAccount  arg: Personal(account={}, password={})",
                  rpcAuthentication.getAccount().getAddress(),
                  sha256AndEncodeHexa(rpcAuthentication.getPassphrase()));
            }

            ListenableFuture<Rpc.SingleBytes> listenableFuture =
                aergoService.exportAccount(rpcAuthentication);
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
