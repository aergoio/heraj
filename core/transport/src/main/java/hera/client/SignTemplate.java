/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.SignAsyncOperation;
import hera.api.SignEitherOperation;
import hera.api.SignOperation;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.key.AergoKey;
import io.grpc.ManagedChannel;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class SignTemplate implements SignOperation {

  protected final SignEitherOperation signEitherOperation;

  public SignTemplate(final ManagedChannel channel, final Context context) {
    this(newFutureStub(channel), context);
  }

  public SignTemplate(final AergoRPCServiceFutureStub aergoService, final Context context) {
    this(new SignEitherTemplate(aergoService, context));
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
