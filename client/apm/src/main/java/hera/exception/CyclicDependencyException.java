/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class CyclicDependencyException extends BuildException {

  public CyclicDependencyException() {
    super();
  }

  public CyclicDependencyException(final String message) {
    super(message);
  }

}
