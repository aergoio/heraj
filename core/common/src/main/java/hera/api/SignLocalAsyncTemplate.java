/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.HerajException;
import hera.exception.NoKeyPresentException;
import hera.key.AergoKey;
import java.util.Optional;
import lombok.Setter;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
public class SignLocalAsyncTemplate implements SignAsyncOperation {

  protected final Logger logger = getLogger(getClass());
  @Setter
  protected Context context;

  @Override
  public ResultOrErrorFuture<Signature> sign(final AergoKey key, final Transaction transaction) {
    return ResultOrErrorFutureFactory.supply(() -> {
      try {
        if (null == key) {
          logger.info("No key for sign");
          return fail(new NoKeyPresentException());
        }
        final Transaction copy = Transaction.copyOf(transaction);
        final BytesValue signature = key.sign(copy.calculateHash().getBytesValue().get());
        copy.setSignature(Signature.of(signature, null));
        return success(Signature.of(signature, copy.calculateHash()));
      } catch (Exception e) {
        return fail(new HerajException(e));
      }
    });
  }

  @Override
  public ResultOrErrorFuture<Boolean> verify(final AergoKey key, final Transaction transaction) {
    return ResultOrErrorFutureFactory.supply(() -> {
      try {
        if (null == key) {
          logger.info("No key for verification");
          return fail(new NoKeyPresentException());
        }
        final Transaction copy = Transaction.copyOf(transaction);
        final BytesValue signature = copy.getSignature().getSign();
        copy.setSignature(null);
        return success(key.verify(copy.calculateHash().getBytesValue().get(), signature));
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
      final SignOperation signOperation = new SignLocalTemplate();
      signOperation.setContext(context);
      return (Optional<T>) Optional.of(signOperation);
    } else if (adaptor.isAssignableFrom(SignEitherOperation.class)) {
      final SignEitherOperation signEitherOperation = new SignLocalEitherTemplate();
      signEitherOperation.setContext(context);
      return (Optional<T>) Optional.of(signEitherOperation);
    }
    return Optional.empty();
  }

}
