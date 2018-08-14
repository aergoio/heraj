/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import hera.build.PackageManager;

public class PackageNotFoundException extends BuildException {
  public PackageNotFoundException(final String message) {
    super(message);
  }

  public PackageNotFoundException(final Throwable cause) {
    super(cause);
  }

}
