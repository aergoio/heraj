/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror.impl;

import hera.api.tupleorerror.Function0;
import hera.api.tupleorerror.WithIdentity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Function0WithIdentity<R> implements Function0<R>, WithIdentity {

  protected final Function0<R> delegate;

  @Getter
  protected final String identity;

  @Override
  public R apply() {
    return delegate.apply();
  }

}
