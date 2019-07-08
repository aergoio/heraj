/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Strategy;
import hera.api.function.Function0;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.function.Function3;
import hera.api.function.Function4;
import hera.api.function.Function5;
import hera.api.function.FunctionDecorator;

public abstract class PreInvocationStrategy implements Strategy, FunctionDecorator {

  public abstract void before(hera.api.function.Function f);

  @Override
  public <R> Function0<R> apply(final Function0<R> f) {
    return new Function0<R>() {
      @Override
      public R apply() {
        before(f);
        return f.apply();
      }
    };
  }

  @Override
  public <T, R> Function1<T, R> apply(final Function1<T, R> f) {
    return new Function1<T, R>() {
      @Override
      public R apply(T t) {
        before(f);
        return f.apply(t);
      }
    };
  }

  @Override
  public <T1, T2, R> Function2<T1, T2, R> apply(final Function2<T1, T2, R> f) {
    return new Function2<T1, T2, R>() {
      @Override
      public R apply(T1 t1, T2 t2) {
        before(f);
        return f.apply(t1, t2);
      }
    };
  }

  @Override
  public <T1, T2, T3, R> Function3<T1, T2, T3, R> apply(final Function3<T1, T2, T3, R> f) {
    return new Function3<T1, T2, T3, R>() {
      @Override
      public R apply(T1 t1, T2 t2, T3 t3) {
        before(f);
        return f.apply(t1, t2, t3);
      }
    };
  }

  @Override
  public <T1, T2, T3, T4, R> Function4<T1, T2, T3, T4, R> apply(
      final Function4<T1, T2, T3, T4, R> f) {
    return new Function4<T1, T2, T3, T4, R>() {
      @Override
      public R apply(T1 t1, T2 t2, T3 t3, T4 t4) {
        before(f);
        return f.apply(t1, t2, t3, t4);
      }
    };
  }

  @Override
  public <T1, T2, T3, T4, T5, R> Function5<T1, T2, T3, T4, T5, R> apply(
      final Function5<T1, T2, T3, T4, T5, R> f) {
    return new Function5<T1, T2, T3, T4, T5, R>() {
      @Override
      public R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        before(f);
        return f.apply(t1, t2, t3, t4, t5);
      }
    };
  }

}
