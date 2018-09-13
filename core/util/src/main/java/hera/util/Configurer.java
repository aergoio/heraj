/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

/**
 * General configurer interface
 *
 * @param <ConfigurableT> target type to configure
 */
public interface Configurer<ConfigurableT> {

  /**
   * Configure {@code target} and return configured result.
   *
   * Return configured result for immutable object.
   *
   * @param target instance to configure
   *
   * @return configured object
   */
  ConfigurableT configure(ConfigurableT target);
}
