/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import hera.Strategy;
import hera.api.function.Function0;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.function.Function3;
import hera.api.function.Function4;
import hera.api.function.Function5;
import hera.api.function.FunctionDecorator;
import hera.exception.DecoratorChainException;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class InvocationStrategy implements Strategy, FunctionDecorator {

  /**
   * An operation wrapping origin function.
   *
   * @param <R> an invocation return type
   *
   * @param f an origin function
   * @param args function arguments
   * @return an invocation return value
   *
   * @throws DecoratorChainException on error in a chain
   */
  protected abstract <R> R wrap(hera.api.function.Function<R> f, List<Object> args);

  @Override
  public <R> Function0<R> apply(final Function0<R> f) {
    return new Function0<R>() {
      @Override
      public R apply() {
        return wrap(f, emptyList());
      }
    };
  }

  @Override
  public <T, R> Function1<T, R> apply(final Function1<T, R> f) {
    return new Function1<T, R>() {
      @Override
      public R apply(final T t) {
        return wrap(f, asList(new Object[] {t}));
      }
    };
  }

  @Override
  public <T1, T2, R> Function2<T1, T2, R> apply(final Function2<T1, T2, R> f) {
    return new Function2<T1, T2, R>() {
      @Override
      public R apply(final T1 t1, final T2 t2) {
        return wrap(f, asList(new Object[] {t1, t2}));
      }
    };
  }

  @Override
  public <T1, T2, T3, R> Function3<T1, T2, T3, R> apply(final Function3<T1, T2, T3, R> f) {
    return new Function3<T1, T2, T3, R>() {
      @Override
      public R apply(final T1 t1, final T2 t2, final T3 t3) {
        return wrap(f, asList(new Object[] {t1, t2, t3}));
      }
    };
  }

  @Override
  public <T1, T2, T3, T4, R> Function4<T1, T2, T3, T4, R> apply(
      final Function4<T1, T2, T3, T4, R> f) {
    return new Function4<T1, T2, T3, T4, R>() {
      @Override
      public R apply(final T1 t1, final T2 t2, final T3 t3, final T4 t4) {
        return wrap(f, asList(new Object[] {t1, t2, t3, t4}));
      }
    };
  }

  @Override
  public <T1, T2, T3, T4, T5, R> Function5<T1, T2, T3, T4, T5, R> apply(
      final Function5<T1, T2, T3, T4, T5, R> f) {
    return new Function5<T1, T2, T3, T4, T5, R>() {
      @Override
      public R apply(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5) {
        return wrap(f, asList(new Object[] {t1, t2, t3, t4, t5}));
      }
    };
  }

}
