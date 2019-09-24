/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.api.function.Function;
import hera.api.function.Functions;
import java.util.List;

public abstract class FailoverStrategy extends InvocationStrategy {

  protected abstract <R> R onFailure(Exception error, Function<R> f, List<Object> args);

  @Override
  protected <R> R wrap(Function<R> f, List<Object> args) {
    try {
      return Functions.invoke(f, args);
    } catch (Exception e) {
      return onFailure(e, f, args);
    }
  }

}
