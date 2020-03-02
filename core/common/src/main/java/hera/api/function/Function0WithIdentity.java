/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class Function0WithIdentity<R> implements Function0<R>, WithIdentity {

  protected final Function0<R> delegate;

  @Getter
  protected final String identity;

  @Override
  public R apply() {
    return delegate.apply();
  }

}
