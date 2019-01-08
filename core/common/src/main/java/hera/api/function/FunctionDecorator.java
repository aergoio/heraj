/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.function;

public interface FunctionDecorator {

  <R> Function0<R> apply(Function0<R> f);

  <T, R> Function1<T, R> apply(Function1<T, R> f);

  <T1, T2, R> Function2<T1, T2, R> apply(Function2<T1, T2, R> f);

  <T1, T2, T3, R> Function3<T1, T2, T3, R> apply(Function3<T1, T2, T3, R> f);

  <T1, T2, T3, T4, R> Function4<T1, T2, T3, T4, R> apply(Function4<T1, T2, T3, T4, R> f);

}
