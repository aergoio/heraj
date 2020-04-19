/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.ExceptionConverter;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public class TransportExceptionConverter implements ExceptionConverter<HerajException> {

  protected final transient Logger logger = getLogger(getClass());

  @Override
  public HerajException convert(final Throwable target) {
    logger.trace("Handle exception {}", target.toString());
    if (target instanceof HerajException) {
      return (HerajException) target;
    } else if (target instanceof StatusRuntimeException) {
      return convertGrpcBasisException((StatusRuntimeException) target);
    } else {
      return new HerajException(target);
    }
  }

  protected HerajException convertGrpcBasisException(final StatusRuntimeException e) {
    switch (e.getStatus().getCode()) {
      case UNAVAILABLE:
        return new ConnectionException(e);
      default:
        return new HerajException(e);
    }
  }


}
