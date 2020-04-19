/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.ExceptionConverter;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public class WalletExceptionConverter implements ExceptionConverter<HerajException> {

  protected final transient Logger logger = getLogger(getClass());

  @Override
  public HerajException convert(final Throwable t) {
    logger.debug("Handle exception {}", t.toString());
    if (t instanceof HerajException) {
      return (HerajException) t;
    } else {
      return new HerajException(t);
    }
  }

}
