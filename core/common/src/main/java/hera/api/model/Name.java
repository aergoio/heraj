/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.internal.AccountAddressAdaptor;
import hera.exception.HerajException;
import hera.util.Adaptor;
import hera.util.StringUtils;
import java.io.UnsupportedEncodingException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@Getter
@ToString
@EqualsAndHashCode
public class Name implements Identity, Adaptor {

  public static final Name EMPTY = Name.of(StringUtils.EMPTY_STRING);
  public static final Name AERGO_SYSTEM = Name.of("aergo.system");
  public static final Name AERGO_NAME = Name.of("aergo.name");

  public static Name of(final String value) {
    return new Name(value);
  }

  protected final BytesValue bytesValue;

  protected final String value;

  /**
   * Create {@code Name}.
   *
   * @param value a name value
   */
  public Name(final String value) {
    assertNotNull(value, "Name value must not null");
    try {
      this.bytesValue = BytesValue.of(value.getBytes("UTF-8"));
      this.value = value;
    } catch (UnsupportedEncodingException e) {
      throw new HerajException(e);
    }
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
