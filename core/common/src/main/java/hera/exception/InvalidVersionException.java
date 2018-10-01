/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class InvalidVersionException extends HerajException {

  private static final long serialVersionUID = 4275076809247024400L;

  protected final byte expectedVersion;
  protected final byte actualVersion;

  public InvalidVersionException(final byte expectedVersion, final byte actualVersion) {
    this.expectedVersion = expectedVersion;
    this.actualVersion = actualVersion;
  }

  @Override
  public String getLocalizedMessage() {
    return String.format("Expected: <0x%02X> but was: <0x%02X>", expectedVersion, actualVersion);
  }

}
