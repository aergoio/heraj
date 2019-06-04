/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.KEYSTORE_CREATE;
import static hera.TransportConstants.KEYSTORE_EXPORTKEY;
import static hera.TransportConstants.KEYSTORE_IMPORTKEY;
import static hera.TransportConstants.KEYSTORE_LIST;
import static hera.TransportConstants.KEYSTORE_LOCK;
import static hera.TransportConstants.KEYSTORE_SIGN;
import static hera.TransportConstants.KEYSTORE_UNLOCK;
import static hera.api.function.Functions.identify;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.KeyStoreOperation;
import hera.api.function.Function0;
import hera.api.function.Function1;
import hera.api.function.Function3;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.client.internal.FinishableFuture;
import hera.client.internal.KeyStoreBaseTemplate;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
public class KeyStoreTemplate
    implements KeyStoreOperation, ChannelInjectable, ContextProviderInjectable {

  @Getter
  protected KeyStoreBaseTemplate keyStoreBaseTemplate = new KeyStoreBaseTemplate();

  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyChain strategyChain = StrategyChain.of(contextProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    getKeyStoreBaseTemplate().setChannel(channel);;
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    getKeyStoreBaseTemplate().setContextProvider(contextProvider);
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<FinishableFuture<List<AccountAddress>>> listFunction =
      getStrategyChain()
          .apply(identify(getKeyStoreBaseTemplate().getListFunction(), KEYSTORE_LIST));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<String, FinishableFuture<AccountAddress>> createFunction =
      getStrategyChain()
          .apply(identify(getKeyStoreBaseTemplate().getCreateFunction(), KEYSTORE_CREATE));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Authentication, FinishableFuture<Boolean>> unlockFunction =
      getStrategyChain()
          .apply(identify(getKeyStoreBaseTemplate().getUnlockFunction(), KEYSTORE_UNLOCK));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Authentication, FinishableFuture<Boolean>> lockFunction =
      getStrategyChain()
          .apply(identify(getKeyStoreBaseTemplate().getLockFunction(), KEYSTORE_LOCK));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<RawTransaction, FinishableFuture<Transaction>> signFunction =
      getStrategyChain()
          .apply(identify(getKeyStoreBaseTemplate().getSignFunction(), KEYSTORE_SIGN));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<EncryptedPrivateKey, String, String,
      FinishableFuture<AccountAddress>> importKeyFunction = getStrategyChain().apply(
          identify(getKeyStoreBaseTemplate().getImportKeyFunction(), KEYSTORE_IMPORTKEY));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Authentication,
      FinishableFuture<EncryptedPrivateKey>> exportKeyFunction = getStrategyChain().apply(
          identify(getKeyStoreBaseTemplate().getExportKeyFunction(), KEYSTORE_EXPORTKEY));

  @Override
  public List<AccountAddress> list() {
    return getListFunction().apply().get();
  }

  @Override
  public AccountAddress create(final String password) {
    return getCreateFunction().apply(password).get();
  }

  @Override
  public boolean lock(final Authentication authentication) {
    return getLockFunction().apply(authentication).get();
  }

  @Override
  public boolean unlock(final Authentication authentication) {
    return getUnlockFunction().apply(authentication).get();
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    return getSignFunction().apply(rawTransaction).get();
  }

  @Override
  public AccountAddress importKey(final EncryptedPrivateKey encryptedKey,
      final String oldPassword, final String newPassword) {
    return getImportKeyFunction().apply(encryptedKey, oldPassword, newPassword).get();
  }

  @Override
  public EncryptedPrivateKey exportKey(final Authentication authentication) {
    return getExportKeyFunction().apply(authentication).get();
  }

}
