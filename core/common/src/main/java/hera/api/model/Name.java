/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.internal.AccountAddressAdaptor;
import hera.util.Adaptor;
import lombok.Getter;

@ApiAudience.Public
@ApiStability.Unstable
public class Name implements Identity, Adaptor {

  public static Name AERGO_SYSTEM = new Name("aergo.system");
  public static Name AERGO_NAME = new Name("aergo.name");

  @ApiAudience.Public
  public static Name of(final String value) {
    return new Name(value);
  }

  @Getter
  protected final String value;

  /**
   * Name constructor.
   *
   * @param value a name value
   */
  @ApiAudience.Public
  public Name(final String value) {
    assertNotNull(value, "Name value must not null");
    this.value = value;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(Name.class)) {
      return (T) this;
    } else if (adaptor.isAssignableFrom(AccountAddress.class)) {
      return (T) new AccountAddressAdaptor(this);
    }
    return null;
  }

}
