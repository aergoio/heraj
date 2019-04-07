/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.key.AergoKey;
import hera.key.Signer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ApiAudience.Private
@ApiStability.Unstable
@ToString(callSuper = true, exclude = "key")
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class AccountWithKey extends AbstractAccount implements Signer {

  @NonNull
  @Getter
  protected final AergoKey key;

  @Override
  public AccountAddress getAddress() {
    return key.getAddress();
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    return key.sign(rawTransaction);
  }

  @Override
  public Signature sign(final BytesValue plainText) {
    return key.sign(plainText);
  }

  @Override
  public String signMessage(final String message) {
    return key.signMessage(message);
  }

}
