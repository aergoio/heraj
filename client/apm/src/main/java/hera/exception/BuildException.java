/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class BuildException extends RuntimeException {
  public BuildException() {
    super();
  }

  public BuildException(final String message) {
    super(message);
  }

  public BuildException(final Throwable cause) {
    super(cause);
  }
}
