/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.function.Functions.identify;
import static hera.client.ClientConstants.KEYSTORE_CREATE;
import static hera.client.ClientConstants.KEYSTORE_EXPORTKEY;
import static hera.client.ClientConstants.KEYSTORE_IMPORTKEY;
import static hera.client.ClientConstants.KEYSTORE_LIST;
import static hera.client.ClientConstants.KEYSTORE_LOCK;
import static hera.client.ClientConstants.KEYSTORE_SIGN;
import static hera.client.ClientConstants.KEYSTORE_UNLOCK;

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
import hera.client.internal.KeyStoreBaseTemplate;
import hera.exception.DecoratorChainException;
import hera.exception.RpcException;
import hera.exception.RpcExceptionConverter;
import hera.strategy.PriorityProvider;
import hera.strategy.StrategyApplier;
import hera.util.ExceptionConverter;
import io.grpc.ManagedChannel;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.concurrent.Future;
import lombok.AccessLevel;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
public class KeyStoreTemplate
    implements KeyStoreOperation, ChannelInjectable, ContextProviderInjectable {

  protected final ExceptionConverter<RpcException> exceptionConverter = new RpcExceptionConverter();

  protected KeyStoreBaseTemplate keyStoreBaseTemplate = new KeyStoreBaseTemplate();

  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyApplier strategyApplier =
      StrategyApplier.of(contextProvider.get(), PriorityProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.keyStoreBaseTemplate.setChannel(channel);;
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    this.keyStoreBaseTemplate.setContextProvider(contextProvider);
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<Future<List<AccountAddress>>> listFunction =
      getStrategyApplier()
          .apply(identify(this.keyStoreBaseTemplate.getListFunction(), KEYSTORE_LIST));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<String, Future<AccountAddress>> createFunction =
      getStrategyApplier()
          .apply(identify(this.keyStoreBaseTemplate.getCreateFunction(), KEYSTORE_CREATE));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Authentication, Future<Boolean>> unlockFunction =
      getStrategyApplier()
          .apply(identify(this.keyStoreBaseTemplate.getUnlockFunction(), KEYSTORE_UNLOCK));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Authentication, Future<Boolean>> lockFunction =
      getStrategyApplier()
          .apply(identify(this.keyStoreBaseTemplate.getLockFunction(), KEYSTORE_LOCK));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<RawTransaction, Future<Transaction>> signFunction =
      getStrategyApplier()
          .apply(identify(this.keyStoreBaseTemplate.getSignFunction(), KEYSTORE_SIGN));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<EncryptedPrivateKey, String, String,
      Future<AccountAddress>> importKeyFunction = getStrategyApplier().apply(
          identify(this.keyStoreBaseTemplate.getImportKeyFunction(), KEYSTORE_IMPORTKEY));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Authentication,
      Future<EncryptedPrivateKey>> exportKeyFunction = getStrategyApplier().apply(
          identify(this.keyStoreBaseTemplate.getExportKeyFunction(), KEYSTORE_EXPORTKEY));

  @Override
  public List<AccountAddress> list() {
    try {
      return getListFunction().apply().get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public AccountAddress create(final String password) {
    try {
      return getCreateFunction().apply(password).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public boolean lock(final Authentication authentication) {
    try {
      return getLockFunction().apply(authentication).get();
    } catch (DecoratorChainException e) {
      if ((e.getCause() instanceof StatusRuntimeException)
          && ((StatusRuntimeException) e.getCause()).getStatus().getCode().equals(Code.UNKNOWN)) {
        return false;
      } else {
        throw exceptionConverter.convert(e);
      }
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public boolean unlock(final Authentication authentication) {
    try {
      return getUnlockFunction().apply(authentication).get();
    } catch (DecoratorChainException e) {
      e.printStackTrace();
      if ((e.getCause() instanceof StatusRuntimeException)
          && ((StatusRuntimeException) e.getCause()).getStatus().getCode().equals(Code.UNKNOWN)) {
        return false;
      } else {
        throw exceptionConverter.convert(e);
      }
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    try {
      return getSignFunction().apply(rawTransaction).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public AccountAddress importKey(final EncryptedPrivateKey encryptedKey,
      final String oldPassword, final String newPassword) {
    try {
      return getImportKeyFunction().apply(encryptedKey, oldPassword, newPassword).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public EncryptedPrivateKey exportKey(final Authentication authentication) {
    try {
      return getExportKeyFunction().apply(authentication).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

}
