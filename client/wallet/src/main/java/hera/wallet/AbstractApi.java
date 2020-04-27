/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static org.slf4j.LoggerFactory.getLogger;

import hera.exception.HerajException;
import hera.exception.WalletExceptionConverter;
import hera.util.ExceptionConverter;
import org.slf4j.Logger;

abstract class AbstractApi {

  protected final transient Logger logger = getLogger(getClass());

  protected final ExceptionConverter<HerajException> converter = new WalletExceptionConverter();

}
