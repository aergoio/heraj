/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import hera.api.model.AccountAddress;
import hera.api.model.Signature;
import hera.key.AergoKey;
import java.io.InputStream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class AccountWithKey extends AbstractAccount implements KeyHoldable {

  @NonNull
  @Getter
  protected final AergoKey key;

  @Override
  public AccountAddress getAddress() {
    return key.getAddress();
  }

  @Override
  public Signature sign(InputStream plainText) {
    return key.sign(plainText);
  }

  @Override
  public boolean verify(InputStream plainText, Signature signature) {
    return key.verify(plainText, signature);
  }

}
