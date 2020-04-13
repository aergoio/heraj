/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.ValidationUtils.assertNotNull;

import lombok.Getter;

public class Response<T> {

  public static <T> Response<T> of(final T value) {
    assertNotNull(value);
    return new Response<>(value, null);
  }

  public static <T> Response<T> of(final Throwable error) {
    assertNotNull(error);
    return new Response<>(null, error);
  }

  @Getter
  protected T value;

  @Getter
  protected Throwable error;

  private Response(final T value, final Throwable error) {
    this.value = value;
    this.error = error;
  }

  public void success(final T value) {
    assertNotNull(value);
    this.value = value;
    this.error = null;
  }

  public void fail(final Throwable error) {
    assertNotNull(error);
    this.value = null;
    this.error = error;
  }
}
