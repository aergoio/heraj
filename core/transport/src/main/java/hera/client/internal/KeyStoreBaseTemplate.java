/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.internal;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.sha256AndEncodeHexa;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function0;
import hera.api.function.Function1;
import hera.api.function.Function3;
import hera.api.function.Function4;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.ChannelInjectable;
import hera.exception.InternalCommitException;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.AuthenticationConverterFactory;
import hera.transport.EncryptedPrivateKeyConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.TransactionConverterFactory;
import io.grpc.ManagedChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
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
  private final Function0<
      Future<List<AccountAddress>>> listFunction = new Function0<Future<List<AccountAddress>>>() {

    @Override
    public Future<List<AccountAddress>> apply() {
      logger.debug("List keystore addresses");

      final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
      logger.trace("AergoService getAccounts arg: {}", empty);

      final Future<AccountOuterClass.AccountList> rawFuture = aergoService.getAccounts(empty);
      final Future<List<AccountAddress>> convertedFuture = HerajFutures.transform(rawFuture,
          new Function1<AccountOuterClass.AccountList, List<AccountAddress>>() {

            @Override
            public List<AccountAddress> apply(
                final AccountOuterClass.AccountList rpcAccountList) {
              final List<AccountAddress> domainAccountList = new ArrayList<>();
              for (final AccountOuterClass.Account rpcAccount : rpcAccountList
                  .getAccountsList()) {
                final AccountAddress domainAccount =
                    accountAddressConverter.convertToDomainModel(rpcAccount.getAddress());
                domainAccountList.add(domainAccount);
              }
              return domainAccountList;
            }
          });
      return convertedFuture;
    }
  };

  @Getter
  private final Function1<String,
      Future<AccountAddress>> createFunction = new Function1<String, Future<AccountAddress>>() {

    @Override
    public Future<AccountAddress> apply(final String password) {
      if (logger.isDebugEnabled()) {
        logger.debug("Create an account to server keystore with password: {}",
            sha256AndEncodeHexa(password));
      }

      final Rpc.Personal rpcPassword =
          Rpc.Personal.newBuilder().setPassphrase(password).build();
      if (logger.isTraceEnabled()) {
        logger.trace("AergoService createAccount arg: {}",
            sha256AndEncodeHexa(rpcPassword.getPassphrase()));
      }

      final Future<AccountOuterClass.Account> rawFuture =
          aergoService.createAccount(rpcPassword);
      final Future<AccountAddress> convertedFuture = HerajFutures.transform(rawFuture,
          new Function1<AccountOuterClass.Account, AccountAddress>() {

            @Override
            public AccountAddress apply(final AccountOuterClass.Account rpcAccount) {
              return accountAddressConverter.convertToDomainModel(rpcAccount.getAddress());
            }
          });
      return convertedFuture;
    }
  };

  @Getter
  private final Function1<Authentication,
      Future<Boolean>> unlockFunction = new Function1<Authentication, Future<Boolean>>() {

    @Override
    public Future<Boolean> apply(final Authentication authentication) {
      logger.debug("Unlock an account in server keystore with authentication: {}",
          authentication);

      final Rpc.Personal rpcAuthentication =
          authenticationConverter.convertToRpcModel(authentication);
      if (logger.isTraceEnabled()) {
        logger.trace("AergoService unlockAccount arg: {}, {}",
            rpcAuthentication.getAccount(),
            sha256AndEncodeHexa(rpcAuthentication.getPassphrase()));
      }

      final Future<AccountOuterClass.Account> rawFuture =
          aergoService.unlockAccount(rpcAuthentication);
      final Future<Boolean> convertedFuture = HerajFutures.transform(rawFuture,
          new Function1<AccountOuterClass.Account, Boolean>() {

            @Override
            public Boolean apply(final AccountOuterClass.Account rpcAccount) {
              return null != rpcAccount.getAddress();
            }
          });
      return convertedFuture;
    }
  };

  @Getter
  private final Function1<Authentication, Future<Boolean>> lockFunction =
      new Function1<Authentication, Future<Boolean>>() {

        @Override
        public Future<Boolean> apply(final Authentication authentication) {
          logger.debug("Lock an account in server keystore with authentication: {}",
              authentication);

          final Rpc.Personal rpcAuthentication =
              authenticationConverter.convertToRpcModel(authentication);
          if (logger.isTraceEnabled()) {
            logger.trace("AergoService lockAccount arg: {}",
                sha256AndEncodeHexa(rpcAuthentication.getPassphrase()));
          }

          final Future<AccountOuterClass.Account> rawFuture =
              aergoService.lockAccount(rpcAuthentication);
          final Future<Boolean> convertedFuture = HerajFutures.transform(rawFuture,
              new Function1<AccountOuterClass.Account, Boolean>() {

                @Override
                public Boolean apply(final AccountOuterClass.Account rpcAccount) {
                  return null != rpcAccount.getAddress();
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function1<RawTransaction,
      Future<Transaction>> signFunction = new Function1<RawTransaction, Future<Transaction>>() {

    @Override
    public Future<Transaction> apply(final RawTransaction rawTransaction) {
      logger.debug("Sign request with rawTx: {}", rawTransaction);

      final Transaction domainTransaction = Transaction.newBuilder()
          .rawTransaction(rawTransaction)
          .signature(Signature.EMPTY)
          .hash(TxHash.of(BytesValue.EMPTY))
          .build();
      final Blockchain.Tx rpcTx = transactionConverter.convertToRpcModel(domainTransaction);
      logger.trace("AergoService signTX arg: {}", rpcTx);

      final Future<Blockchain.Tx> rawFuture = aergoService.signTX(rpcTx);
      final Future<Transaction> convertedFuture =
          HerajFutures.transform(rawFuture, new Function1<Blockchain.Tx, Transaction>() {
            @Override
            public Transaction apply(final Blockchain.Tx tx) {
              return transactionConverter.convertToDomainModel(tx);
            }
          });
      return convertedFuture;
    }
  };

  @Getter
  private final Function3<EncryptedPrivateKey, String, String,
      Future<AccountAddress>> importKeyFunction = new Function3<EncryptedPrivateKey, String, String,
      Future<AccountAddress>>() {

    @Override
    public Future<AccountAddress> apply(final EncryptedPrivateKey encryptedKey,
        final String oldPassword, final String newPassword) {
      if (logger.isDebugEnabled()) {
        logger.debug(
            "Import an account to server keystore with "
                + "encryptedKey: {}, oldPassword: {}, newPassword: {}",
            encryptedKey, sha256AndEncodeHexa(oldPassword),
            sha256AndEncodeHexa(newPassword));
      }

      final Rpc.ImportFormat rpcImport = Rpc.ImportFormat.newBuilder()
          .setWif(encryptedPkConverter.convertToRpcModel(encryptedKey))
          .setOldpass(oldPassword).setNewpass(newPassword).build();
      if (logger.isTraceEnabled()) {
        logger.trace(
            "AergoService importAccount arg: ImportFormat(wif={}, oldPass={}, newPass={})",
            rpcImport.getWif(), sha256AndEncodeHexa(rpcImport.getOldpass()),
            sha256AndEncodeHexa(rpcImport.getNewpass()));
      }

      final Future<AccountOuterClass.Account> rawFuture =
          aergoService.importAccount(rpcImport);
      final Future<AccountAddress> convertedFuture = HerajFutures.transform(rawFuture,
          new Function1<AccountOuterClass.Account, AccountAddress>() {

            @Override
            public AccountAddress apply(final AccountOuterClass.Account rpcAccount) {
              return accountAddressConverter
                  .convertToDomainModel(rpcAccount.getAddress());
            }
          });
      return convertedFuture;
    }
  };

  @Getter
  private final Function1<Authentication, Future<EncryptedPrivateKey>> exportKeyFunction =
      new Function1<Authentication, Future<EncryptedPrivateKey>>() {

        @Override
        public Future<EncryptedPrivateKey> apply(final Authentication authentication) {
          logger.debug("Export an account from server keystore with authentication: {}",
              authentication);

          final Rpc.Personal rpcAuthentication =
              authenticationConverter.convertToRpcModel(authentication);
          if (logger.isTraceEnabled()) {
            logger.trace("AergoService exportAccount  arg: Personal(account={}, password={})",
                rpcAuthentication.getAccount().getAddress(),
                sha256AndEncodeHexa(rpcAuthentication.getPassphrase()));
          }

          final Future<Rpc.SingleBytes> rawFuture =
              aergoService.exportAccount(rpcAuthentication);
          final Future<EncryptedPrivateKey> convertedFuture = HerajFutures.transform(rawFuture,
              new Function1<Rpc.SingleBytes, EncryptedPrivateKey>() {

                @Override
                public EncryptedPrivateKey apply(final Rpc.SingleBytes rawPk) {
                  return encryptedPkConverter.convertToDomainModel(rawPk);
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function4<AccountAddress, AccountAddress, Aer, BytesValue, Future<TxHash>>
      sendFunction =
      new Function4<AccountAddress, AccountAddress, Aer, BytesValue, Future<TxHash>>() {

        @Override
        public Future<TxHash> apply(final AccountAddress sender,
            final AccountAddress recipient, final Aer amount, final BytesValue payload) {
          logger.debug("Send transaction request with sender: {},"
              + "recipient: {}, amount: {}, payload: {}", sender, recipient, amount, payload);

          final RawTransaction rawTransaction = RawTransaction.newBuilder()
              .chainIdHash(contextProvider.get().getChainIdHash())
              .from(sender)
              .to(recipient)
              .amount(amount)
              .nonce(0L)
              .payload(payload)
              .build();
          final Transaction transaction = Transaction.newBuilder()
              .rawTransaction(rawTransaction)
              .signature(Signature.EMPTY)
              .hash(TxHash.of(BytesValue.EMPTY))
              .build();
          final Blockchain.Tx rpcTx = transactionConverter.convertToRpcModel(transaction);
          logger.trace("AergoService sendTX arg: {}", rpcTx);

          final Future<Rpc.CommitResult> rawFuture = aergoService.sendTX(rpcTx);
          final Future<TxHash> convertedFuture =
              HerajFutures.transform(rawFuture, new Function1<Rpc.CommitResult, TxHash>() {

                @Override
                public TxHash apply(final Rpc.CommitResult rpcCommitResult) {
                  if (Rpc.CommitStatus.TX_OK != rpcCommitResult.getError()) {
                    throw new InternalCommitException(rpcCommitResult.getError(),
                        rpcCommitResult.getDetail());
                  }
                  return new TxHash(of(rpcCommitResult.getHash().toByteArray()));
                }
              });
          return convertedFuture;
        }
      };

}
