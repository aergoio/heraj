/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.key.AergoKey;
import hera.transaction.TxSigner;
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
public class AccountWithKey extends AbstractAccount implements TxSigner {

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

}
