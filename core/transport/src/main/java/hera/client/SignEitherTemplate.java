/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.tupleorerror.FunctionChain.fail;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.SignAsyncOperation;
import hera.api.SignEitherOperation;
import hera.api.SignOperation;
import hera.api.model.Signature;
import hera.api.model.Time;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.HerajException;
import hera.key.AergoKey;
import hera.strategy.TimeoutStrategy;
import io.grpc.ManagedChannel;
import java.util.Optional;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
public class SignEitherTemplate implements SignEitherOperation, ChannelInjectable {

  protected Context context;

  protected SignAsyncTemplate signAsyncOperation = new SignAsyncTemplate();

  @Getter(lazy = true)
  private final Time timeout =
      context.getStrategy(TimeoutStrategy.class).map(TimeoutStrategy::getTimeout).get();

  @Override
  public void setContext(final Context context) {
    this.context = context;
    signAsyncOperation.setContext(context);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    this.signAsyncOperation.injectChannel(channel);
  }

  @Override
  public ResultOrError<Signature> sign(final AergoKey key, final Transaction transaction) {
    try {
      return signAsyncOperation.sign(key, transaction).get(getTimeout().getValue(),
          getTimeout().getUnit());
    } catch (Exception e) {
      return fail(new HerajException(e));
    }
  }

  @Override
  public ResultOrError<Boolean> verify(final AergoKey key, final Transaction transaction) {
    try {
      return signAsyncOperation.verify(key, transaction).get(getTimeout().getValue(),
          getTimeout().getUnit());
    } catch (Exception e) {
      return fail(new HerajException(e));
    }
  }

  @Override
  public <T> Optional<T> adapt(final Class<T> adaptor) {
    if (adaptor.isAssignableFrom(SignEitherOperation.class)) {
      return (Optional<T>) Optional.of(this);
    } else if (adaptor.isAssignableFrom(SignOperation.class)) {
      final SignTemplate signOperation = new SignTemplate();
      signOperation.setContext(context);
      signOperation
          .injectChannel((ManagedChannel) signAsyncOperation.getAergoService().getChannel());
      return (Optional<T>) Optional.of(signOperation);
    } else if (adaptor.isAssignableFrom(SignAsyncOperation.class)) {
      return (Optional<T>) Optional.of(signAsyncOperation);
    }
    return Optional.empty();
  }

}
