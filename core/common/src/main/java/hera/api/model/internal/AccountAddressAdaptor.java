/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.Name;

/**
 * Just to use name in a account address. I want to remove it.
 *
 * @author taeiklim
 *
 */
public class AccountAddressAdaptor extends AccountAddress {

  protected final Name name;

  public AccountAddressAdaptor(final Name name) {
    assertNotNull(name);
    this.name = name;
  }

  @Override
  public BytesValue getBytesValue() {
    return this.name.getBytesValue();
  }

  @Override
  public String getValue() {
    return this.name.getValue();
  }

}
