/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import static org.slf4j.LoggerFactory.getLogger;

import hera.util.ExceptionConverter;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;

public class RpcExceptionConverter implements ExceptionConverter<RpcException> {

  protected final transient Logger logger = getLogger(getClass());

  @Override
  public RpcException convert(final Throwable rawError) {
    logger.trace("Handle exception {}", rawError.toString());

    Throwable target = rawError;
    if (target instanceof DecoratorChainException) {
      target = target.getCause();
    }

    // handle commit exception specially
    // NOTE: a custom exception throws by callback must handle like this
    // FIXME: no way to improve it?
    if (target instanceof InternalCommitException) {
      final InternalCommitException internalCommitException = (InternalCommitException) target;
      return new RpcCommitException(internalCommitException);
    }

    if (target instanceof RpcException) {
      return (RpcException) target;
    } else if (target instanceof StatusRuntimeException) {
      return convertGrpcBasisException((StatusRuntimeException) target);
    } else {
      return new RpcException(target);
    }
  }

  protected RpcException convertGrpcBasisException(final StatusRuntimeException e) {
    switch (e.getStatus().getCode()) {
      case UNAVAILABLE:
        return new RpcConnectionException(e);
      default:
        return new RpcException(e);
    }
  }


}
