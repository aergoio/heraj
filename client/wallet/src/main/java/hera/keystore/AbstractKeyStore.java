/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Authentication;
import hera.exception.HerajException;
import hera.exception.WalletExceptionConverter;
import hera.key.AergoKey;
import hera.util.ExceptionConverter;
import org.slf4j.Logger;

abstract class AbstractKeyStore implements KeyStore {

  protected final transient Logger logger = getLogger(getClass());

  protected final ExceptionConverter<HerajException> converter = new WalletExceptionConverter();

  abstract protected AergoKey loadAergoKey(Authentication authentication);
}
