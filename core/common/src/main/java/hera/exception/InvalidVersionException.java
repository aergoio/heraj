/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class InvalidVersionException extends HerajException {

  private static final long serialVersionUID = 4275076809247024400L;

  protected byte expectedVersion = 0;
  protected byte actualVersion = 0;

  public InvalidVersionException(final byte expectedVersion, final byte actualVersion) {
    this.expectedVersion = expectedVersion;
    this.actualVersion = actualVersion;
  }

  public InvalidVersionException(final String message) {
    super(message);
  }

  @Override
  public String getLocalizedMessage() {
    if (0 == expectedVersion && 0 == actualVersion) {
      return getMessage();
    }
    return String.format("Expected: <0x%02X> but was: <0x%02X>", expectedVersion, actualVersion);
  }

}
