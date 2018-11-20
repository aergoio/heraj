/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Strategy;
import hera.api.tupleorerror.Function0;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function2;
import hera.api.tupleorerror.Function3;
import hera.api.tupleorerror.Function4;
import hera.api.tupleorerror.FunctionDecorator;

public interface PostInvocationStrategy extends Strategy, FunctionDecorator {

  <R> R after(R r);

  @Override
  default <R> Function0<R> apply(Function0<R> f) {
    return f.andThen(r -> after(r));
  }

  @Override
  default <T, R> Function1<T, R> apply(Function1<T, R> f) {
    return f.andThen(r -> after(r));
  }

  @Override
  default <T1, T2, R> Function2<T1, T2, R> apply(Function2<T1, T2, R> f) {
    return f.andThen(r -> after(r));
  }

  @Override
  default <T1, T2, T3, R> Function3<T1, T2, T3, R> apply(Function3<T1, T2, T3, R> f) {
    return f.andThen(r -> after(r));
  }

  @Override
  default <T1, T2, T3, T4, R> Function4<T1, T2, T3, T4, R> apply(
      Function4<T1, T2, T3, T4, R> f) {
    return f.andThen(r -> after(r));
  }

}
