/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static hera.client.Methods.KEYSTORE_CREATE;
import static hera.client.Methods.KEYSTORE_EXPORTKEY;
import static hera.client.Methods.KEYSTORE_IMPORTKEY;
import static hera.client.Methods.KEYSTORE_LIST;
import static hera.client.Methods.KEYSTORE_LOCK;
import static hera.client.Methods.KEYSTORE_SEND;
import static hera.client.Methods.KEYSTORE_SIGN;
import static hera.client.Methods.KEYSTORE_UNLOCK;
import static hera.util.TransportUtils.sha256AndEncodeHexa;
import static org.slf4j.LoggerFactory.getLogger;

import hera.RequestMethod;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.exception.CommitException;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.AuthenticationConverterFactory;
import hera.transport.EncryptedPrivateKeyConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.TransactionConverterFactory;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import types.AccountOuterClass;
import types.Blockchain;
import types.Rpc;

class KeyStoreMethods extends AbstractMethods {

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
  protected final RequestMethod<List<AccountAddress>> list =
      new RequestMethod<List<AccountAddress>>() {

        @Getter
        protected final String name = KEYSTORE_LIST;

        @Override
        protected List<AccountAddress> runInternal(final List<Object> parameters) throws Exception {
          logger.debug("List keystore addresses");

          final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
          logger.trace("AergoService getAccounts arg: {}", empty);

          final AccountOuterClass.AccountList rpcAccountList = getBlockingStub().getAccounts(empty);
          final List<AccountAddress> domainAccountList = new ArrayList<>();
          for (final AccountOuterClass.Account rpcAccount : rpcAccountList
              .getAccountsList()) {
            final AccountAddress domainAccount =
                accountAddressConverter.convertToDomainModel(rpcAccount.getAddress());
            domainAccountList.add(domainAccount);
          }
          return domainAccountList;
        }
      };

  @Getter
  protected final RequestMethod<AccountAddress> create = new RequestMethod<AccountAddress>() {

    @Getter
    protected final String name = KEYSTORE_CREATE;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, String.class);
    }

    @Override
    protected AccountAddress runInternal(final List<Object> parameters) throws Exception {
      final String password = (String) parameters.get(0);
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

      final AccountOuterClass.Account rpcAccount = getBlockingStub().createAccount(rpcPassword);
      return accountAddressConverter.convertToDomainModel(rpcAccount.getAddress());
    }
  };


  @Getter
  protected final RequestMethod<Boolean> unlock = new RequestMethod<Boolean>() {

    @Getter
    protected final String name = KEYSTORE_LOCK;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, Authentication.class);
    }

    @Override
    protected Boolean runInternal(final List<Object> parameters) throws Exception {
      final Authentication authentication = (Authentication) parameters.get(0);
      logger.debug("Unlock an account in server keystore with authentication: {}",
          authentication);

      final Rpc.Personal rpcAuthentication = authenticationConverter
          .convertToRpcModel(authentication);
      if (logger.isTraceEnabled()) {
        logger.trace("AergoService unlockAccount arg: {}, {}",
            rpcAuthentication.getAccount(),
            sha256AndEncodeHexa(rpcAuthentication.getPassphrase()));
      }

      try {
        final AccountOuterClass.Account rpcAccount = getBlockingStub()
            .unlockAccount(rpcAuthentication);
        return null != rpcAccount.getAddress();
      } catch (StatusRuntimeException e) {
        // TODO: keystore operations will be removed.
        System.out.println(e.getMessage());
        if (!e.getMessage().contains("address or password is incorrect")) {
          throw e;
        }
        return false;
      }
    }

  };

  @Getter
  protected final RequestMethod<Boolean> lock = new RequestMethod<Boolean>() {

    @Getter
    protected final String name = KEYSTORE_UNLOCK;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, Authentication.class);
    }

    @Override
    protected Boolean runInternal(final List<Object> parameters) throws Exception {
      final Authentication authentication = (Authentication) parameters.get(0);
      logger.debug("Lock an account in server keystore with authentication: {}",
          authentication);

      final Rpc.Personal rpcAuthentication =
          authenticationConverter.convertToRpcModel(authentication);
      if (logger.isTraceEnabled()) {
        logger.trace("AergoService lockAccount arg: {}",
            sha256AndEncodeHexa(rpcAuthentication.getPassphrase()));
      }

      try {
        final AccountOuterClass.Account rpcAccount = getBlockingStub()
            .lockAccount(rpcAuthentication);
        return null != rpcAccount.getAddress();
      } catch (StatusRuntimeException e) {
        // TODO: keystore operations will be removed.
        if (!e.getMessage().contains("address or password is incorrect")) {
          throw e;
        }
        return false;
      }
    }

  };

  @Getter
  protected final RequestMethod<Transaction> sign = new RequestMethod<Transaction>() {

    @Getter
    protected final String name = KEYSTORE_SIGN;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, RawTransaction.class);
    }

    @Override
    protected Transaction runInternal(final List<Object> parameters) throws Exception {
      final RawTransaction rawTransaction = (RawTransaction) parameters.get(0);
      logger.debug("Sign request with rawTx: {}", rawTransaction);

      final Transaction domainTransaction = Transaction.newBuilder()
          .rawTransaction(rawTransaction)
          .signature(Signature.EMPTY)
          .hash(TxHash.of(BytesValue.EMPTY))
          .build();
      final Blockchain.Tx rpcTx = transactionConverter.convertToRpcModel(domainTransaction);
      logger.trace("AergoService signTX arg: {}", rpcTx);

      final Blockchain.Tx rpcSignedTx = getBlockingStub().signTX(rpcTx);
      return transactionConverter.convertToDomainModel(rpcSignedTx);
    }
  };

  @Getter
  protected final RequestMethod<AccountAddress> importKey = new RequestMethod<AccountAddress>() {

    @Getter
    protected final String name = KEYSTORE_IMPORTKEY;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, EncryptedPrivateKey.class);
      validateType(parameters, 1, String.class);
      validateType(parameters, 2, String.class);
    }

    @Override
    protected AccountAddress runInternal(final List<Object> parameters) throws Exception {
      final EncryptedPrivateKey encryptedKey = (EncryptedPrivateKey) parameters.get(0);
      final String oldPassword = (String) parameters.get(1);
      final String newPassword = (String) parameters.get(2);
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

      final AccountOuterClass.Account rpcAccount = getBlockingStub().importAccount(rpcImport);
      return accountAddressConverter
          .convertToDomainModel(rpcAccount.getAddress());
    }

  };

  @Getter
  protected final RequestMethod<EncryptedPrivateKey> exportKey =
      new RequestMethod<EncryptedPrivateKey>() {

        @Getter
        protected final String name = KEYSTORE_EXPORTKEY;

        @Override
        protected void validate(final List<Object> parameters) {
          validateType(parameters, 0, Authentication.class);
        }

        @Override
        protected EncryptedPrivateKey runInternal(final List<Object> parameters) throws Exception {
          final Authentication authentication = (Authentication) parameters.get(0);
          logger.debug("Export an account from server keystore with authentication: {}",
              authentication);

          final Rpc.Personal rpcAuthentication =
              authenticationConverter.convertToRpcModel(authentication);
          if (logger.isTraceEnabled()) {
            logger.trace("AergoService exportAccount  arg: Personal(account={}, password={})",
                rpcAuthentication.getAccount().getAddress(),
                sha256AndEncodeHexa(rpcAuthentication.getPassphrase()));
          }

          final Rpc.SingleBytes rawPk = getBlockingStub().exportAccount(rpcAuthentication);
          return encryptedPkConverter.convertToDomainModel(rawPk);
        }

      };

  @Getter
  protected final RequestMethod<TxHash> send = new RequestMethod<TxHash>() {

    @Getter
    protected final String name = KEYSTORE_SEND;

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, AccountAddress.class);
      validateType(parameters, 1, AccountAddress.class);
      validateType(parameters, 2, Aer.class);
      validateType(parameters, 3, BytesValue.class);
    }

    @Override
    protected TxHash runInternal(final List<Object> parameters) throws Exception {
      final AccountAddress sender = (AccountAddress) parameters.get(0);
      final AccountAddress recipient = (AccountAddress) parameters.get(1);
      final Aer amount = (Aer) parameters.get(2);
      final BytesValue payload = (BytesValue) parameters.get(3);
      logger.debug("Send transaction request with sender: {},"
          + "recipient: {}, amount: {}, payload: {}", sender, recipient, amount, payload);

      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .chainIdHash(getChainIdHash())
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

      final Rpc.CommitResult rpcCommitResult = getBlockingStub().sendTX(rpcTx);
      if (Rpc.CommitStatus.TX_OK != rpcCommitResult.getError()) {
        throw new CommitException(rpcCommitResult.getError(),
            rpcCommitResult.getDetail());
      }
      return new TxHash(of(rpcCommitResult.getHash().toByteArray()));
    }

  };

}
