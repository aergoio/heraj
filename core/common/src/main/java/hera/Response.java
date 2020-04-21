/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.Getter;

/**
 * A response class.
 *
 * @param <ValueT> a type of response
 */
@ApiAudience.Private
@ApiStability.Unstable
public class Response<ValueT> {

  public static <T> Response<T> success(final T value) {
    return new Response<>(value, null);
  }

  public static <T> Response<T> fail(final Exception error) {
    assertNotNull(error, "Error must not null");
    return new Response<>(null, error);
  }

  @Getter
  protected ValueT value;

  @Getter
  protected Exception error;

  private Response(final ValueT value, final Exception error) {
    this.value = value;
    this.error = error;
  }

}
