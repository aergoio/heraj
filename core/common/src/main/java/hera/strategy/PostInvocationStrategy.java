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
import hera.api.function.FunctionDecorator;
import hera.api.function.Functions;

public abstract class PostInvocationStrategy implements Strategy, FunctionDecorator {

  public abstract <R> R after(hera.api.function.Function f, R r);

  @Override
  public <R> Function0<R> apply(final Function0<R> f) {
    return Functions.compose(f, new Function1<R, R>() {
      public R apply(R r) {
        after(f, r);
        return r;
      }
    });
  }

  @Override
  public <T, R> Function1<T, R> apply(final Function1<T, R> f) {
    return Functions.compose(f, new Function1<R, R>() {
      public R apply(R r) {
        after(f, r);
        return r;
      }
    });
  }

  @Override
  public <T1, T2, R> Function2<T1, T2, R> apply(final Function2<T1, T2, R> f) {
    return Functions.compose(f, new Function1<R, R>() {
      public R apply(R r) {
        after(f, r);
        return r;
      }
    });
  }

  @Override
  public <T1, T2, T3, R> Function3<T1, T2, T3, R> apply(final Function3<T1, T2, T3, R> f) {
    return Functions.compose(f, new Function1<R, R>() {
      public R apply(R r) {
        after(f, r);
        return r;
      }
    });
  }

  @Override
  public <T1, T2, T3, T4, R> Function4<T1, T2, T3, T4, R> apply(
      final Function4<T1, T2, T3, T4, R> f) {
    return Functions.compose(f, new Function1<R, R>() {
      public R apply(R r) {
        after(f, r);
        return r;
      }
    });
  }

}
