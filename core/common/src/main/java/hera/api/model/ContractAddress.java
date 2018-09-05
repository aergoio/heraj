/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import java.util.Optional;

public class ContractAddress extends AccountAddress {

  public static ContractAddress of(final byte[] bytes) {
    return new ContractAddress(bytes);
  }

  public ContractAddress(final byte[] value) {
    super(value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(ContractAddress.class)) {
      return (Optional<T>) Optional.of(this);
    }
    return Optional.empty();
  }

}
