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
  public RpcException convert(final Throwable t) {
    logger.debug("Handle exception {}", t.toString());
    if (t instanceof RpcException) {
      return (RpcException) t;
    } else if (t instanceof StatusRuntimeException) {
      return convertGrpcBasisException((StatusRuntimeException) t);
    } else {
      return new RpcException(t);
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
