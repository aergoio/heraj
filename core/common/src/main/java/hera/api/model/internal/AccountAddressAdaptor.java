/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

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
    super(BytesValue.of(name.getValue().getBytes()), name.getValue());
    this.name = name;
  }

}
