/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class DecodingFailureException extends HerajException {

  private static final long serialVersionUID = -5006002440529452290L;

  public enum Format {
    Hexa,
    Base58,
    Base58WithCheck
  }

  protected final String source;

  protected final Format format;

  public DecodingFailureException(final String source, final Format format) {
    this.source = source;
    this.format = format;
  }

  @Override
  public String getLocalizedMessage() {
    return String.format("Decoding %s with %s failure", source, format.toString());
  }

}
