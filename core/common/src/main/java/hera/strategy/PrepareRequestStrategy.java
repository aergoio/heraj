/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.api.function.Function;
import hera.api.function.Functions;
import java.util.List;

public abstract class PrepareRequestStrategy extends InvocationStrategy {

  protected abstract void prepare(List<Object> args);

  @Override
  protected <R> R wrap(final Function<R> f, final List<Object> args) {
    prepare(args);
    return Functions.invoke(f, args);
  }

}
