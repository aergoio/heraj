/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.Getter;

/**
 * A mutable response class.
 *
 * @param <T> an type of response
 */
@ApiAudience.Private
@ApiStability.Unstable
public class Response<T> {

  public static <T> Response<T> empty() {
    return new Response<>(null, null);
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
   * @return an instance of this
   */
  public Response<T> success(final T value) {
    this.value = value;
    this.error = null;
    return this;
  }

  /**
   * Fail response with an error.
   *
   * @param error an error
   * @return an instance of this
   */
  public Response<T> fail(final Exception error) {
    assertNotNull(error);
    this.value = null;
    this.error = error;
    return this;
  }

}
