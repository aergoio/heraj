/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static hera.api.tupleorerror.FunctionChain.fail;
import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.SignAsyncOperation;
import hera.api.SignEitherOperation;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.HerajException;
import io.grpc.ManagedChannel;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class SignEitherTemplate implements SignEitherOperation {

  protected final SignAsyncOperation signAsyncOperation;

  public SignEitherTemplate(final ManagedChannel channel, final Context context) {
    this(newFutureStub(channel), context);
  }

  public SignEitherTemplate(final AergoRPCServiceFutureStub aergoService, final Context context) {
    this(new SignAsyncTemplate(aergoService, context));
  }

  @Override
  public ResultOrError<Signature> sign(final Transaction transaction) {
    try {
      return signAsyncOperation.sign(transaction).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new HerajException(e));
    }
  }

  @Override
  public ResultOrError<Boolean> verify(final Transaction transaction) {
    try {
      return signAsyncOperation.verify(transaction).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new HerajException(e));
    }
  }

}
