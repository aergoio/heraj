/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.Hash;
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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class AccountWithAddressAndSigner extends AbstractAccount implements Signer {

  @NonNull
  @Getter
  protected final AccountAddress address;

  @NonNull
  @Getter
  protected final Signer delegate;

  @Override
  public AergoKey getKey() {
    return null;
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    return delegate.sign(rawTransaction);
  }

  @Override
  public Signature sign(final Hash hash) {
    return delegate.sign(hash);
  }

  @Override
  public String signMessage(final String message) {
    return delegate.signMessage(message);
  }

  @Override
  public Signature signMessage(final BytesValue message) {
    return delegate.signMessage(message);
  }

}
