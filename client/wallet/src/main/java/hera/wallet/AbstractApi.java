/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.exception.HerajException;
import hera.exception.WalletExceptionConverter;
import hera.util.ExceptionConverter;

abstract class AbstractApi {

  protected final ExceptionConverter<HerajException> converter = new WalletExceptionConverter();

}
