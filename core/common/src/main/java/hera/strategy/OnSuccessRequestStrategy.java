/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.api.function.Function;
import hera.api.function.Functions;
import java.util.List;

public abstract class OnSuccessRequestStrategy extends InvocationStrategy {

  protected abstract <R> R onSuccess(R r);

  @Override
  protected <R> R wrap(final Function<R> f, final List<Object> args) {
    final R r = Functions.invoke(f, args);
    return onSuccess(r);
  }

}
