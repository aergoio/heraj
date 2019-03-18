/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

public interface ExceptionConverter<ExceptionT> {

  /**
   * Convert exception.
   *
   * @param t an exception to convert
   * @return converted exception
   */
  ExceptionT convert(Throwable t);

}
