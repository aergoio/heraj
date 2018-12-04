/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.ACCOUNT_CREATE;
import static hera.TransportConstants.ACCOUNT_EXPORTKEY;
import static hera.TransportConstants.ACCOUNT_GETSTATE;
import static hera.TransportConstants.ACCOUNT_IMPORTKEY;
import static hera.TransportConstants.ACCOUNT_LIST;
import static hera.TransportConstants.ACCOUNT_LOCK;
import static hera.TransportConstants.ACCOUNT_SIGN;
import static hera.TransportConstants.ACCOUNT_UNLOCK;
import static hera.TransportConstants.ACCOUNT_VERIFY;
import static hera.api.tupleorerror.Functions.identify;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AccountOperation;
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
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@ApiAudience.Private
@ApiStability.Unstable
public class AccountTemplate
    implements AccountOperation, ChannelInjectable, ContextProviderInjectable {

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
      getStrategyChain().apply(identify(getAccountBaseTemplate().getListFunction(), ACCOUNT_LIST));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<String, ResultOrErrorFuture<Account>> createFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getCreateFunction(), ACCOUNT_CREATE));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<AccountAddress, ResultOrErrorFuture<AccountState>> stateFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getStateFunction(), ACCOUNT_GETSTATE));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Authentication, ResultOrErrorFuture<Boolean>> unlockFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getUnlockFunction(), ACCOUNT_UNLOCK));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Account, Transaction, ResultOrErrorFuture<Signature>> signFunction =
      getStrategyChain().apply(identify(getAccountBaseTemplate().getSignFunction(), ACCOUNT_SIGN));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Account, Transaction, ResultOrErrorFuture<Boolean>> verifyFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getVerifyFunction(), ACCOUNT_VERIFY));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Authentication, ResultOrErrorFuture<Boolean>> lockFunction =
      getStrategyChain().apply(identify(getAccountBaseTemplate().getLockFunction(), ACCOUNT_LOCK));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<EncryptedPrivateKey, String, String,
      ResultOrErrorFuture<Account>> importKeyFunction = getStrategyChain().apply(
          identify(getAccountBaseTemplate().getImportKeyFunction(), ACCOUNT_IMPORTKEY));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Authentication,
      ResultOrErrorFuture<EncryptedPrivateKey>> exportKeyFunction = getStrategyChain().apply(
          identify(getAccountBaseTemplate().getExportKeyFunction(), ACCOUNT_EXPORTKEY));

  @Override
  public List<AccountAddress> list() {
    return getListFunction().apply().get().getResult();
  }

  @Override
  public Account create(final String password) {
    return getCreateFunction().apply(password).get().getResult();
  }

  @Override
  public AccountState getState(final AccountAddress address) {
    return getStateFunction().apply(address).get().getResult();
  }

  @Override
  public boolean lock(final Authentication authentication) {
    return getLockFunction().apply(authentication).get().getResult();
  }

  @Override
  public boolean unlock(final Authentication authentication) {
    return getUnlockFunction().apply(authentication).get().getResult();
  }

  @Override
  public Signature sign(final Account account, final Transaction transaction) {
    return getSignFunction().apply(account, transaction).get().getResult();
  }

  @Override
  public boolean verify(final Account account, final Transaction transaction) {
    return getVerifyFunction().apply(account, transaction).get().getResult();
  }

  @Override
  public Account importKey(final EncryptedPrivateKey encryptedKey,
      final String oldPassword, final String newPassword) {
    return getImportKeyFunction().apply(encryptedKey, oldPassword, newPassword).get().getResult();
  }

  @Override
  public EncryptedPrivateKey exportKey(final Authentication authentication) {
    return getExportKeyFunction().apply(authentication).get().getResult();
  }

}
