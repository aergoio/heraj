/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.api.tupleorerror.FunctionChain.of;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.ListenableFuture;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.tupleorerror.Function0;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function3;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.transport.AccountConverterFactory;
import hera.transport.AuthenticationConverterFactory;
import hera.transport.EncryptedPrivateKeyConverterFactory;
import hera.transport.ModelConverter;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import types.AccountOuterClass;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
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
  private final Function0<ResultOrErrorFuture<List<AccountAddress>>> listFunction =
      () -> {
        ResultOrErrorFuture<List<AccountAddress>> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();
        logger.debug("List keystore address, Context: {}", contextProvider.get());

        ListenableFuture<AccountOuterClass.AccountList> listenableFuture =
            aergoService.getAccounts(Rpc.Empty.newBuilder().build());
        FutureChain<AccountOuterClass.AccountList, List<AccountAddress>> callback =
            new FutureChain<>(nextFuture, contextProvider.get());
        callback.setSuccessHandler(list -> of(
            () -> list.getAccountsList().stream().map(accountConverter::convertToDomainModel)
                .map(Account::getAddress).collect(toList())));
        addCallback(listenableFuture, callback, directExecutor());

        return nextFuture;
      };

  @Getter
  private final Function1<String, ResultOrErrorFuture<Account>> createFunction =
      (password) -> {
        ResultOrErrorFuture<Account> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();
        logger.debug("Create an account to server keystore, Context: {}", contextProvider.get());

        Rpc.Personal personal = Rpc.Personal.newBuilder().setPassphrase(password).build();
        ListenableFuture<AccountOuterClass.Account> listenableFuture =
            aergoService.createAccount(personal);
        FutureChain<AccountOuterClass.Account, Account> callback =
            new FutureChain<>(nextFuture, contextProvider.get());
        callback.setSuccessHandler(
            account -> of(() -> accountConverter.convertToDomainModel(account)));
        addCallback(listenableFuture, callback, directExecutor());

        return nextFuture;
      };

  @Getter
  private final Function1<Authentication, ResultOrErrorFuture<Boolean>> unlockFunction =
      (authentication) -> {
        ResultOrErrorFuture<Boolean> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();
        logger.debug("Unlock an account in server keystore, Address: {}, Context: {}",
            authentication.getAddress(), contextProvider.get());

        ListenableFuture<AccountOuterClass.Account> listenableFuture =
            aergoService
                .unlockAccount(authenticationConverter.convertToRpcModel(authentication));
        FutureChain<AccountOuterClass.Account, Boolean> callback =
            new FutureChain<>(nextFuture, contextProvider.get());
        callback.setSuccessHandler(account -> of(() -> null != account.getAddress()));
        addCallback(listenableFuture, callback, directExecutor());

        return nextFuture;
      };
  @Getter
  private final Function1<Authentication, ResultOrErrorFuture<Boolean>> lockFunction =
      (authentication) -> {
        ResultOrErrorFuture<Boolean> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();
        logger.debug("Lock an account in server keystore, Address: {}, Context: {}",
            authentication.getAddress(), contextProvider.get());

        ListenableFuture<AccountOuterClass.Account> listenableFuture =
            aergoService.lockAccount(authenticationConverter.convertToRpcModel(authentication));
        FutureChain<AccountOuterClass.Account, Boolean> callback =
            new FutureChain<>(nextFuture, contextProvider.get());
        callback.setSuccessHandler(account -> of(() -> null != account.getAddress()));
        addCallback(listenableFuture, callback, directExecutor());

        return nextFuture;
      };

  @Getter
  private final Function3<EncryptedPrivateKey, String, String,
      ResultOrErrorFuture<Account>> importKeyFunction =
          (encryptedKey, oldPassword, newPassword) -> {
            ResultOrErrorFuture<Account> nextFuture =
                ResultOrErrorFutureFactory.supplyEmptyFuture();
            logger.debug("Import an account to server keystore, EncryptedKey: {}, Context: {}",
                encryptedKey, contextProvider.get());

            Rpc.ImportFormat importFormat = Rpc.ImportFormat.newBuilder()
                .setWif(encryptedPkConverter.convertToRpcModel(encryptedKey))
                .setOldpass(oldPassword)
                .setNewpass(newPassword).build();
            ListenableFuture<AccountOuterClass.Account> listenableFuture =
                aergoService.importAccount(importFormat);
            FutureChain<AccountOuterClass.Account, Account> callback =
                new FutureChain<>(nextFuture, contextProvider.get());
            callback.setSuccessHandler(
                account -> of(() -> accountConverter.convertToDomainModel(account)));
            addCallback(listenableFuture, callback, directExecutor());

            return nextFuture;
          };

  @Getter
  private final Function1<Authentication,
      ResultOrErrorFuture<EncryptedPrivateKey>> exportKeyFunction = (authentication) -> {
        ResultOrErrorFuture<EncryptedPrivateKey> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();
        logger.debug("Export an account from server keystore, Address: {}, Context: {}",
            authentication.getAddress(), contextProvider.get());

        ListenableFuture<Rpc.SingleBytes> listenableFuture =
            aergoService
                .exportAccount(authenticationConverter.convertToRpcModel(authentication));
        FutureChain<Rpc.SingleBytes, EncryptedPrivateKey> callback =
            new FutureChain<>(nextFuture, contextProvider.get());
        callback.setSuccessHandler(
            rawPk -> of(() -> encryptedPkConverter.convertToDomainModel(rawPk)));
        addCallback(listenableFuture, callback, directExecutor());

        return nextFuture;
      };

}
