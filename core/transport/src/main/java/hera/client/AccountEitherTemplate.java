/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.ACCOUNT_CREATE_EITHER;
import static hera.TransportConstants.ACCOUNT_EXPORTKEY_EITHER;
import static hera.TransportConstants.ACCOUNT_GETSTATE_EITHER;
import static hera.TransportConstants.ACCOUNT_IMPORTKEY_EITHER;
import static hera.TransportConstants.ACCOUNT_LIST_EITHER;
import static hera.TransportConstants.ACCOUNT_LOCK_EITHER;
import static hera.TransportConstants.ACCOUNT_SIGN_EITHER;
import static hera.TransportConstants.ACCOUNT_UNLOCK_EITHER;
import static hera.TransportConstants.ACCOUNT_VERIFY_EITHER;
import static hera.api.tupleorerror.Functions.identify;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AccountEitherOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.Function0;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function2;
import hera.api.tupleorerror.Function3;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@ApiAudience.Private
@ApiStability.Unstable
public class AccountEitherTemplate
    implements AccountEitherOperation, ChannelInjectable, ContextProviderInjectable {

  @Getter
  protected AccountBaseTemplate accountBaseTemplate = new AccountBaseTemplate();

  @Setter
  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyChain strategyChain = StrategyChain.of(contextProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    getAccountBaseTemplate().setChannel(channel);;
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<ResultOrErrorFuture<List<AccountAddress>>> listFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getListFunction(), ACCOUNT_LIST_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<String, ResultOrErrorFuture<Account>> createFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getCreateFunction(), ACCOUNT_CREATE_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<AccountAddress, ResultOrErrorFuture<AccountState>> stateFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getStateFunction(), ACCOUNT_GETSTATE_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Authentication, ResultOrErrorFuture<Boolean>> unlockFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getUnlockFunction(), ACCOUNT_UNLOCK_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Account, Transaction, ResultOrErrorFuture<Signature>> signFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getSignFunction(), ACCOUNT_SIGN_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Account, Transaction, ResultOrErrorFuture<Boolean>> verifyFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getVerifyFunction(), ACCOUNT_VERIFY_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Authentication, ResultOrErrorFuture<Boolean>> lockFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getLockFunction(), ACCOUNT_LOCK_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<EncryptedPrivateKey, String, String,
      ResultOrErrorFuture<Account>> importKeyFunction = getStrategyChain().apply(
          identify(getAccountBaseTemplate().getImportKeyFunction(), ACCOUNT_IMPORTKEY_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Authentication,
      ResultOrErrorFuture<EncryptedPrivateKey>> exportKeyFunction = getStrategyChain().apply(
          identify(getAccountBaseTemplate().getExportKeyFunction(), ACCOUNT_EXPORTKEY_EITHER));

  @Override
  public ResultOrError<List<AccountAddress>> list() {
    return getListFunction().apply().get();
  }

  @Override
  public ResultOrError<Account> create(final String password) {
    return getCreateFunction().apply(password).get();
  }

  @Override
  public ResultOrError<AccountState> getState(final AccountAddress address) {
    return getStateFunction().apply(address).get();
  }

  @Override
  public ResultOrError<Boolean> lock(final Authentication authentication) {
    return getLockFunction().apply(authentication).get();
  }

  @Override
  public ResultOrError<Boolean> unlock(final Authentication authentication) {
    return getUnlockFunction().apply(authentication).get();
  }

  @Override
  public ResultOrError<Signature> sign(final Account account, final Transaction transaction) {
    return getSignFunction().apply(account, transaction).get();
  }

  @Override
  public ResultOrError<Boolean> verify(final Account account, final Transaction transaction) {
    return getVerifyFunction().apply(account, transaction).get();
  }

  @Override
  public ResultOrError<Account> importKey(final EncryptedPrivateKey encryptedKey,
      final String oldPassword, final String newPassword) {
    return getImportKeyFunction().apply(encryptedKey, oldPassword, newPassword).get();
  }

  @Override
  public ResultOrError<EncryptedPrivateKey> exportKey(final Authentication authentication) {
    return getExportKeyFunction().apply(authentication).get();
  }

}
