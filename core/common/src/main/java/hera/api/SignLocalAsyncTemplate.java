/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.HerajException;
import hera.key.AergoKey;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class SignLocalAsyncTemplate implements SignAsyncOperation {

  protected final Context context;

  @Override
  public ResultOrErrorFuture<Signature> sign(final AergoKey key, final Transaction transaction) {
    return ResultOrErrorFutureFactory.supply(() -> {
      try {
        final BytesValue signature = key.sign(transaction.calculateHash().getBytesValue().get());
        transaction.setSignature(Signature.of(signature, null));
        return success(Signature.of(signature, transaction.calculateHash()));
      } catch (Exception e) {
        return fail(new HerajException(e));
      }
    });
  }

  @Override
  public ResultOrErrorFuture<Boolean> verify(final AergoKey key, final Transaction transaction) {
    return ResultOrErrorFutureFactory.supply(() -> {
      try {
        final BytesValue signature = transaction.getSignature().getSign();
        transaction.setSignature(null);
        return success(key.verify(transaction.calculateHash().getBytesValue().get(), signature));
      } catch (Exception e) {
        return fail(new HerajException(e));
      }
    });
  }

  @Override
  public <T> Optional<T> adapt(final Class<T> adaptor) {
    if (adaptor.isAssignableFrom(SignAsyncOperation.class)) {
      return (Optional<T>) Optional.of(this);
    } else if (adaptor.isAssignableFrom(SignOperation.class)) {
      return (Optional<T>) Optional.of(new SignLocalTemplate(new SignLocalEitherTemplate(this)));
    } else if (adaptor.isAssignableFrom(SignEitherOperation.class)) {
      return (Optional<T>) Optional.of(new SignLocalEitherTemplate(this));
    }
    return Optional.empty();
  }

}
