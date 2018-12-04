/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import hera.api.tupleorerror.impl.Function0WithIdentity;
import hera.api.tupleorerror.impl.Function1WithIdentity;
import hera.api.tupleorerror.impl.Function2WithIdentity;
import hera.api.tupleorerror.impl.Function3WithIdentity;
import hera.api.tupleorerror.impl.Function4WithIdentity;

public final class Functions {

  /**
   * Identify a function by name.
   *
   * @param <R> a function return type
   * @param f a function to identify
   * @param identification an identification
   * @return a function with identification
   */
  public static <R> Function0<R> identify(final Function0<R> f, final String identification) {
    return new Function0WithIdentity<>(f, identification);
  }

  /**
   * Identify a function by name.
   *
   * @param <T> a function argument type
   * @param <R> a function return type
   * @param f a function to identify
   * @param identification an identification
   * @return a function with identification
   */
  public static <T, R> Function1<T, R> identify(final Function1<T, R> f,
      final String identification) {
    return new Function1WithIdentity<>(f, identification);
  }

  /**
   * Identify a function by name.
   *
   * @param <T1> a function 1st argument type
   * @param <T2> a function 2nd argument type
   * @param <R> a function return type
   * @param f a function to identify
   * @param identification an identification
   * @return a function with identification
   */
  public static <T1, T2, R> Function2<T1, T2, R> identify(final Function2<T1, T2, R> f,
      final String identification) {
    return new Function2WithIdentity<>(f, identification);
  }

  /**
   * Identify a function by name.
   *
   * @param <T1> a function 1st argument type
   * @param <T2> a function 2nd argument type
   * @param <T3> a function 3rd argument type
   * @param <R> a function return type
   * @param f a function to identify
   * @param identification an identification
   * @return a function with identification
   */
  public static <T1, T2, T3, R> Function3<T1, T2, T3, R> identify(final Function3<T1, T2, T3, R> f,
      final String identification) {
    return new Function3WithIdentity<>(f, identification);
  }

  /**
   * Identify a function by name.
   *
   * @param <T1> a function 1st argument type
   * @param <T2> a function 2nd argument type
   * @param <T3> a function 3rd argument type
   * @param <T4> a function 4th argument type
   * @param <R> a function return type
   * @param f a function to identify
   * @param identification an identification
   * @return a function with identification
   */
  public static <T1, T2, T3, T4, R> Function4<T1, T2, T3, T4, R> identify(
      final Function4<T1, T2, T3, T4, R> f, final String identification) {
    return new Function4WithIdentity<>(f, identification);
  }

}
