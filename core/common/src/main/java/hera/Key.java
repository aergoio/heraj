/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class Key<ValueT> {

  /**
   * Create a {@code Key} instance.
   *
   * @param name       an name of key
   * @param valueClass a class of value
   * @param <T>        a type of value
   * @return a key instance
   */
  public static <T> Key<T> of(final String name, final Class<T> valueClass) {
    return new Key(name, valueClass);
  }

  @Getter
  protected final String name;

  @Getter
  protected final Class<ValueT> valueClass;

  private Key(final String name, final Class<ValueT> valueClass) {
    assertNotNull(name, "Name must not null");
    assertNotNull(valueClass, "Value class must not null");
    this.name = name;
    this.valueClass = valueClass;
  }

}
