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
import hera.api.tupleorerror.FunctionDecoratorChain;

public interface PostInvocationStrategy extends Strategy, FunctionDecorator {

  <R> R after(R r);

  @Override
  default <R> Function0<R> applyNext(Function0<R> f, FunctionDecoratorChain chain) {
    final Function0<R> applied = f.andThen(r -> after(r));
    return chain == null ? applied : chain.apply(applied);
  }

  @Override
  default <T, R> Function1<T, R> applyNext(Function1<T, R> f, FunctionDecoratorChain chain) {
    final Function1<T, R> applied = f.andThen(r -> after(r));
    return chain == null ? applied : chain.apply(applied);
  }

  @Override
  default <T1, T2, R> Function2<T1, T2, R> applyNext(Function2<T1, T2, R> f,
      FunctionDecoratorChain chain) {
    final Function2<T1, T2, R> applied = f.andThen(r -> after(r));
    return chain == null ? applied : chain.apply(applied);
  }

  @Override
  default <T1, T2, T3, R> Function3<T1, T2, T3, R> applyNext(Function3<T1, T2, T3, R> f,
      FunctionDecoratorChain chain) {
    final Function3<T1, T2, T3, R> applied = f.andThen(r -> after(r));
    return chain == null ? applied : chain.apply(applied);
  }

  @Override
  default <T1, T2, T3, T4, R> Function4<T1, T2, T3, T4, R> applyNext(Function4<T1, T2, T3, T4, R> f,
      FunctionDecoratorChain chain) {
    final Function4<T1, T2, T3, T4, R> applied = f.andThen(r -> after(r));
    return chain == null ? applied : chain.apply(applied);
  }

}
