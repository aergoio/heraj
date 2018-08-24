/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class PackageNotFoundException extends BuildException {
  public PackageNotFoundException(final String message) {
    super(message);
  }

  public PackageNotFoundException(final Throwable cause) {
    super(cause);
  }

}
