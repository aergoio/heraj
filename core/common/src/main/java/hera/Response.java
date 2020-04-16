/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.Getter;

@ApiAudience.Public
@ApiStability.Unstable
public class Response<T> {

  protected static Response<?> EMPTY = new Response<>(null, null);

  @SuppressWarnings("unchecked")
  public static <T> Response<T> empty() {
    return (Response<T>) EMPTY;
  }

  public static <T> Response<T> of(final T value) {
    assertNotNull(value);
    return new Response<>(value, null);
  }

  public static <T> Response<T> of(final Exception error) {
    assertNotNull(error);
    return new Response<>(null, error);
  }

  @Getter
  protected T value;

  @Getter
  protected Exception error;

  private Response(final T value, final Exception error) {
    this.value = value;
    this.error = error;
  }

  /**
   * Success response with a value.
   *
   * @param value a value. Can be null.
   */
  public void success(final T value) {
    this.value = value;
    this.error = null;
  }

  /**
   * Fail response with an error.
   *
   * @param error an error
   */
  public void fail(final Exception error) {
    assertNotNull(error);
    this.value = null;
    this.error = error;
  }

}
