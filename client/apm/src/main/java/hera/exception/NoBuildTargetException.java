/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class NoBuildTargetException extends BuildException {
  public NoBuildTargetException() {
    super("No build target");
  }
}
