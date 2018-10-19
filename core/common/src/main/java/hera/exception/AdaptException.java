/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

public class AdaptException extends HerajException {

  private static final long serialVersionUID = -4646404321907745575L;

  protected Class<?> from;
  protected Class<?> to;

  public AdaptException(final Class<?> from, final Class<?> to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public String getLocalizedMessage() {
    return String.format("Adapting from %s to %s failed", from.toString(), to.toString());
  }

  @Override
  public AdaptException clone() {
    return new AdaptException(from, to);
  }

}
