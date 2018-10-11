/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.SignEitherOperation;
import hera.api.SignOperation;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import io.grpc.ManagedChannel;
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
  public Signature sign(final Transaction transaction) {
    return signEitherOperation.sign(transaction).getResult();
  }

  @Override
  public boolean verify(final Transaction transaction) {
    return signEitherOperation.verify(transaction).getResult();
  }

}
