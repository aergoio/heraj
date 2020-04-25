/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.exception.HerajException;
import hera.exception.WalletExceptionConverter;
import hera.util.ExceptionConverter;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public abstract class AbstractKeyStore {

  protected final transient Logger logger = getLogger(getClass());

  protected final ExceptionConverter<HerajException> converter = new WalletExceptionConverter();

}
