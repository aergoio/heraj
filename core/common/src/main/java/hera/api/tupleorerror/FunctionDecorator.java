/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

public interface FunctionDecorator {

  <R> Function0<R> applyNext(Function0<R> f, FunctionDecoratorChain chain);

  <T, R> Function1<T, R> applyNext(Function1<T, R> f, FunctionDecoratorChain chain);

  <T1, T2, R> Function2<T1, T2, R> applyNext(Function2<T1, T2, R> f, FunctionDecoratorChain chain);

  <T1, T2, T3, R> Function3<T1, T2, T3, R> applyNext(Function3<T1, T2, T3, R> f,
      FunctionDecoratorChain chain);

  <T1, T2, T3, T4, R> Function4<T1, T2, T3, T4, R> applyNext(Function4<T1, T2, T3, T4, R> f,
      FunctionDecoratorChain chain);

}
