/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static hera.api.tupleorerror.FunctionChain.fail;
import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.SignAsyncOperation;
import hera.api.SignOperation;
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
public class SignTemplate implements SignOperation {

  protected final SignAsyncOperation signAsyncOperation;

  public SignTemplate(final ManagedChannel channel) {
    this(newFutureStub(channel));
  }

  public SignTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(new SignAsyncTemplate(aergoService));
  }

  @Override
  public ResultOrError<Signature> sign(Transaction transaction) {
    try {
      return signAsyncOperation.sign(transaction).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new HerajException(e));
    }
  }

  @Override
  public ResultOrError<Boolean> verify(Transaction transaction) {
    try {
      return signAsyncOperation.verify(transaction).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new HerajException(e));
    }
  }

}
