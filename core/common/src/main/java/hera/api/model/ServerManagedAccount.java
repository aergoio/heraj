/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class ServerManagedAccount extends AbstractAccount {

  public static ServerManagedAccount of(final AccountAddress address) {
    return new ServerManagedAccount(address);
  }

  @Setter
  @Getter
  protected AccountAddress address = new AccountAddress(BytesValue.EMPTY);

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(ServerManagedAccount.class)) {
      return (Optional<T>) Optional.of(this);
    } else if (adaptor.isAssignableFrom(AccountAddress.class)) {
      return (Optional<T>) Optional.of(getAddress());
    }
    return Optional.empty();
  }

}
