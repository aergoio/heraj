/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static hera.api.tupleorerror.FunctionChain.fail;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.HerajException;
import hera.key.AergoKey;
import java.util.Optional;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
public class SignLocalEitherTemplate implements SignEitherOperation {

  protected Context context;

  protected SignLocalAsyncTemplate signAsyncOperation = new SignLocalAsyncTemplate();

  @Override
  public void setContext(Context context) {
    this.context = context;
    signAsyncOperation.setContext(context);
  }

  @Override
  public ResultOrError<Signature> sign(final AergoKey key, final Transaction transaction) {
    try {
      return signAsyncOperation.sign(key, transaction).get();
    } catch (Exception e) {
      return fail(new HerajException(e));
    }
  }

  @Override
  public ResultOrError<Boolean> verify(final AergoKey key, final Transaction transaction) {
    try {
      return signAsyncOperation.verify(key, transaction).get();
    } catch (Exception e) {
      return fail(new HerajException(e));
    }
  }

  @Override
  public <T> Optional<T> adapt(final Class<T> adaptor) {
    if (adaptor.isAssignableFrom(SignEitherOperation.class)) {
      return (Optional<T>) Optional.of(this);
    } else if (adaptor.isAssignableFrom(SignOperation.class)) {
      final SignOperation signOperation = new SignLocalTemplate();
      signOperation.setContext(context);
      return (Optional<T>) Optional.of(signOperation);
    } else if (adaptor.isAssignableFrom(SignAsyncOperation.class)) {
      return (Optional<T>) Optional.of(signAsyncOperation);
    }
    return Optional.empty();
  }

}
