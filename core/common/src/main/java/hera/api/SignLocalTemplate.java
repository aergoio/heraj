/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.key.AergoKey;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class SignLocalTemplate implements SignOperation {

  @Getter
  protected final SignEitherOperation signEitherOperation;

  public SignLocalTemplate(final Context context) {
    this(new SignLocalEitherTemplate(context));
  }

  @Override
  public Signature sign(final AergoKey key, final Transaction transaction) {
    return signEitherOperation.sign(key, transaction).getResult();
  }

  @Override
  public boolean verify(final AergoKey key, final Transaction transaction) {
    return signEitherOperation.verify(key, transaction).getResult();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> adapt(final Class<T> adaptor) {
    if (adaptor.isAssignableFrom(SignOperation.class)) {
      return (Optional<T>) Optional.of(this);
    } else if (adaptor.isAssignableFrom(SignEitherOperation.class)) {
      return (Optional<T>) Optional.of(signEitherOperation);
    } else if (adaptor.isAssignableFrom(SignAsyncOperation.class)) {
      return (Optional<T>) signEitherOperation.adapt(SignAsyncOperation.class);
    }
    return Optional.empty();
  }

}
