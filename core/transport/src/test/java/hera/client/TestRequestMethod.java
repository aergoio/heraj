/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;

import hera.RequestMethod;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class TestRequestMethod<T> extends RequestMethod<T> {

  static <T> RequestMethod<T> success(final T t) {
    return new TestRequestMethod<>(t, null);
  }

  static <T> RequestMethod<T> fail(final Exception error) {
    return new TestRequestMethod<>(null, error);
  }

  @Getter
  protected final String name = randomUUID().toString();

  protected final T ret;
  protected final Exception error;

  @Override
  protected T runInternal(final List<Object> parameters) throws Exception {
    if (null != error) {
      throw error;
    }
    return ret;
  }
}
