/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.function;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.function.impl.Function0WithIdentity;
import hera.api.function.impl.Function1WithIdentity;
import hera.api.function.impl.Function2WithIdentity;
import hera.api.function.impl.Function3WithIdentity;
import hera.api.function.impl.Function4WithIdentity;
import hera.api.function.impl.Function5WithIdentity;
import hera.exception.HerajException;
import java.util.List;

public final class Functions {

  protected static final Class<?>[] functions = {
      Function0.class,
      Function1.class,
      Function2.class,
      Function3.class,
      Function4.class,
      Function5.class,
  };

  /**
   * Invoke target {@code f} with {@code args} and return result.
   *
   * @param <R> an invocation return type
   *
   * @param f a function to invoke
   * @param args arguments to pass
   * @return invocation result
   */
  public static <R> R invoke(final Function<R> f, final List<Object> args) {
    return buildInvocation(f, args).apply();
  }

  /**
   * Make invocation with {@code f} and {@code args}.
   *
   * @param <R> an invocation return type
   *
   * @param f a function to invoke
   * @param args arguments to pass
   * @return invocation object
   */
  @SuppressWarnings("unchecked")
  public static <R> Function0<R> buildInvocation(final Function<R> f, final List<Object> args) {
    assertNotNull(f);
    assertNotNull(args);

    if (functions.length < args.size()) {
      throw new HerajException("Invalid arguments length for target " + f);
    }

    final Class<?> possibleTarget = functions[args.size()];
    if (!possibleTarget.isInstance(f)) {
      throw new HerajException("Invalid arguments length for target " + f);
    }

    return new Function0<R>() {

      @Override
      public R apply() {
        R ret = null;
        if (f instanceof Function0) {
          ret = ((Function0<R>) f)
              .apply();
        } else if (f instanceof Function1) {
          ret = ((Function1<Object, R>) f)
              .apply(args.get(0));
        } else if (f instanceof Function2) {
          ret = ((Function2<Object, Object, R>) f)
              .apply(args.get(0), args.get(1));
        } else if (f instanceof Function3) {
          ret = ((Function3<Object, Object, Object, R>) f)
              .apply(args.get(0), args.get(1), args.get(2));
        } else if (f instanceof Function4) {
          ret = ((Function4<Object, Object, Object, Object, R>) f)
              .apply(args.get(0), args.get(1), args.get(2), args.get(3));
        } else if (f instanceof Function5) {
          ret = ((Function5<Object, Object, Object, Object, Object, R>) f)
              .apply(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4));
        }
        return ret;
      }
    };
  }

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
   * Identify a function by name.
   *
   * @param <T1> a function 1st argument type
   * @param <T2> a function 2nd argument type
   * @param <T3> a function 3rd argument type
   * @param <T4> a function 4th argument type
   * @param <T5> a function 5th argument type
   * @param <R> a function return type
   *
   * @param f a function to identify
   * @param identity an identity
   *
   * @return a function with identity
   */
  public static <T1, T2, T3, T4, T5, R> Function5<T1, T2, T3, T4, T5, R> identify(
      final Function5<T1, T2, T3, T4, T5, R> f, final String identity) {
    return new Function5WithIdentity<T1, T2, T3, T4, T5, R>(f, identity);
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

  /**
   * Returns a composed function that first applies {@code first} function to its input, and then
   * applies the {@code second} function to the result.
   *
   * @param <T1> the 1st input type of {@code first}
   * @param <T2> the 2nd input type of {@code first}
   * @param <T3> the 3rd input type of {@code first}
   * @param <T4> the 3rd input type of {@code first}
   * @param <T5> the 4th input type of {@code first}
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
  public static <T1, T2, T3, T4, T5, R, V> Function5<T1, T2, T3, T4, T5, V> compose(
      final Function5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5,
          ? extends R> first,
      final Function1<? super R, ? extends V> second) {
    if (null == first) {
      throw new NullPointerException();
    }
    if (null == second) {
      throw new NullPointerException();
    }
    return new Function5<T1, T2, T3, T4, T5, V>() {
      @Override
      public V apply(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5) {
        return second.apply(first.apply(t1, t2, t3, t4, t5));
      }
    };
  }

}
