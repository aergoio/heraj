/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.key.AergoKey;
import java.io.InputStream;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class ClientManagedAccount extends AbstractAccount implements KeyHoldable {

  public static ClientManagedAccount of(final AergoKey key) {
    return new ClientManagedAccount(key);
  }

  @NonNull
  @Getter
  protected final AergoKey key;

  @Override
  public AccountAddress getAddress() {
    return key.getAddress();
  }

  @Override
  public BytesValue sign(InputStream plainText) {
    return key.sign(plainText);
  }

  @Override
  public boolean verify(InputStream plainText, BytesValue signature) {
    return key.verify(plainText, signature);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(ClientManagedAccount.class)) {
      return (Optional<T>) Optional.of(this);
    } else if (adaptor.isAssignableFrom(AccountAddress.class)) {
      return (Optional<T>) Optional.of(getAddress());
    }
    return Optional.empty();
  }

}
