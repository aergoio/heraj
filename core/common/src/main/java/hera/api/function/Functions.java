/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.function;

import hera.api.function.impl.Function0WithIdentity;
import hera.api.function.impl.Function1WithIdentity;
import hera.api.function.impl.Function2WithIdentity;
import hera.api.function.impl.Function3WithIdentity;
import hera.api.function.impl.Function4WithIdentity;

public final class Functions {

  /**
   * Identify a function by name.
   *
   * @param <R> a function return type
   *
   * @param f a function to identify
   * @param identity an identity
   *
   * @return a function with identity
   */
  public static <R> Function0<R> identify(final Function0<R> f, final String identity) {
    return new Function0WithIdentity<R>(f, identity);
  }

  /**
   * Identify a function by name.
   *
   * @param <T> a function argument type
   * @param <R> a function return type
   *
   * @param f a function to identify
   * @param identity an identity
   *
   * @return a function with identity
   */
  public static <T, R> Function1<T, R> identify(final Function1<T, R> f,
      final String identity) {
    return new Function1WithIdentity<T, R>(f, identity);
  }

  /**
   * Identify a function by name.
   *
   * @param <T1> a function 1st argument type
   * @param <T2> a function 2nd argument type
   * @param <R> a function return type
   *
   * @param f a function to identify
   * @param identity an identity
   *
   * @return a function with identity
   */
  public static <T1, T2, R> Function2<T1, T2, R> identify(final Function2<T1, T2, R> f,
      final String identity) {
    return new Function2WithIdentity<T1, T2, R>(f, identity);
  }

  /**
   * Identify a function by name.
   *
   * @param <T1> a function 1st argument type
   * @param <T2> a function 2nd argument type
   * @param <T3> a function 3rd argument type
   * @param <R> a function return type
   *
   * @param f a function to identify
   * @param identity an identity
   *
   * @return a function with identity
   */
  public static <T1, T2, T3, R> Function3<T1, T2, T3, R> identify(final Function3<T1, T2, T3, R> f,
      final String identity) {
    return new Function3WithIdentity<T1, T2, T3, R>(f, identity);
  }

  /**
   * Identify a function by name.
   *
   * @param <T1> a function 1st argument type
   * @param <T2> a function 2nd argument type
   * @param <T3> a function 3rd argument type
   * @param <T4> a function 4th argument type
   * @param <R> a function return type
   *
   * @param f a function to identify
   * @param identity an identity
   *
   * @return a function with identity
   */
  public static <T1, T2, T3, T4, R> Function4<T1, T2, T3, T4, R> identify(
      final Function4<T1, T2, T3, T4, R> f, final String identity) {
    return new Function4WithIdentity<T1, T2, T3, T4, R>(f, identity);
  }

  /**
   * Returns a composed function that first applies {@code first} function to its input, and then
   * applies the {@code second} function to the result.
   *
   * @param <R> the output type of {@code first} and the input type of {@code second}
   * @param <V> the type of output of the {@code second} function
   *
   * @param first the function to invoke first
   * @param second the function to invoke second
   *
   * @return a composed function that first applies {@code first} function and then applies the
   *         {@code second} function
   *
   * @throws NullPointerException if before or after is null
   */
  public static <R, V> Function0<V> compose(final Function0<R> first,
      final Function1<? super R, ? extends V> second) {
    if (null == first) {
      throw new NullPointerException();
    }
    if (null == second) {
      throw new NullPointerException();
    }
    return new Function0<V>() {
      @Override
      public V apply() {
        return second.apply(first.apply());
      }
    };
  }

  /**
   * Returns a composed function that first applies {@code first} function to its input, and then
   * applies the {@code second} function to the result.
   *
   * @param <T> the input type of {@code first}
   * @param <R> the output type of {@code first} and the input type of {@code second}
   * @param <V> the type of output of the {@code second} function
   *
   * @param first the function to invoke first
   * @param second the function to invoke second
   *
   * @return a composed function that first applies {@code first} function and then applies the
   *         {@code second} function
   *
   * @throws NullPointerException if before or after is null
   */
  public static <T, R, V> Function1<T, V> compose(final Function1<? super T, ? extends R> first,
      final Function1<? super R, ? extends V> second) {
    if (null == first) {
      throw new NullPointerException();
    }
    if (null == second) {
      throw new NullPointerException();
    }
    return new Function1<T, V>() {
      @Override
      public V apply(final T t) {
        return second.apply(first.apply(t));
      }
    };
  }

  /**
   * Returns a composed function that first applies {@code first} function to its input, and then
   * applies the {@code second} function to the result.
   *
   * @param <T1> the 1st input type of {@code first}
   * @param <T2> the 2nd input type of {@code first}
   * @param <R> the output type of {@code first} and the input type of {@code second}
   * @param <V> the type of output of the {@code second} function
   *
   * @param first the function to invoke first
   * @param second the function to invoke second
   *
   * @return a composed function that first applies {@code first} function and then applies the
   *         {@code second} function
   *
   * @throws NullPointerException if before or after is null
   */
  public static <T1, T2, R, V> Function2<T1, T2, V> compose(
      final Function2<? super T1, ? super T2, ? extends R> first,
      final Function1<? super R, ? extends V> second) {
    if (null == first) {
      throw new NullPointerException();
    }
    if (null == second) {
      throw new NullPointerException();
    }
    return new Function2<T1, T2, V>() {
      @Override
      public V apply(final T1 t1, final T2 t2) {
        return second.apply(first.apply(t1, t2));
      }
    };
  }

  /**
   * Returns a composed function that first applies {@code first} function to its input, and then
   * applies the {@code second} function to the result.
   *
   * @param <T1> the 1st input type of {@code first}
   * @param <T2> the 2nd input type of {@code first}
   * @param <T3> the 3rd input type of {@code first}
   * @param <R> the output type of {@code first} and the input type of {@code second}
   * @param <V> the type of output of the {@code second} function
   *
   * @param first the function to invoke first
   * @param second the function to invoke second
   *
   * @return a composed function that first applies {@code first} function and then applies the
   *         {@code second} function
   *
   * @throws NullPointerException if before or after is null
   */
  public static <T1, T2, T3, R, V> Function3<T1, T2, T3, V> compose(
      final Function3<? super T1, ? super T2, ? super T3, ? extends R> first,
      final Function1<? super R, ? extends V> second) {
    if (null == first) {
      throw new NullPointerException();
    }
    if (null == second) {
      throw new NullPointerException();
    }
    return new Function3<T1, T2, T3, V>() {
      @Override
      public V apply(final T1 t1, final T2 t2, final T3 t3) {
        return second.apply(first.apply(t1, t2, t3));
      }
    };
  }

  /**
   * Returns a composed function that first applies {@code first} function to its input, and then
   * applies the {@code second} function to the result.
   *
   * @param <T1> the 1st input type of {@code first}
   * @param <T2> the 2nd input type of {@code first}
   * @param <T3> the 3rd input type of {@code first}
   * @param <T4> the 3rd input type of {@code first}
   * @param <R> the output type of {@code first} and the input type of {@code second}
   * @param <V> the type of output of the {@code second} function
   *
   * @param first the function to invoke first
   * @param second the function to invoke second
   *
   * @return a composed function that first applies {@code first} function and then applies the
   *         {@code second} function
   *
   * @throws NullPointerException if before or after is null
   */
  public static <T1, T2, T3, T4, R, V> Function4<T1, T2, T3, T4, V> compose(
      final Function4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> first,
      final Function1<? super R, ? extends V> second) {
    if (null == first) {
      throw new NullPointerException();
    }
    if (null == second) {
      throw new NullPointerException();
    }
    return new Function4<T1, T2, T3, T4, V>() {
      @Override
      public V apply(final T1 t1, final T2 t2, final T3 t3, final T4 t4) {
        return second.apply(first.apply(t1, t2, t3, t4));
      }
    };
  }

}
