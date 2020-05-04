/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.Hash;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.exception.HerajException;
import hera.key.Signer;

class ProxySigner implements Signer, LockableCabinet<Signer> {

  protected Signer delegate;

  ProxySigner() {

  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    assertNotNull(rawTransaction, "Raw transaction must not null");
    final Signer unlocked = getUnlocked();
    if (!unlocked.getPrincipal().equals(rawTransaction.getSender())) {
      throw new HerajException("Sender of the rawTransaction should equals with signer");
    }
    return unlocked.sign(rawTransaction);
  }

  @Override
  public Signature signMessage(final BytesValue message) {
    assertNotNull(message, "Message must not null");
    final Signer unlocked = getUnlocked();
    return unlocked.signMessage(message);
  }

  @Override
  public Signature signMessage(final Hash hashedMessage) {
    assertNotNull(hashedMessage, "Hashed message must not null");
    final Signer unlocked = getUnlocked();
    return unlocked.signMessage(hashedMessage);
  }

  @Override
  public AccountAddress getPrincipal() {
    final Signer unlocked = getUnlocked();
    return unlocked.getPrincipal();
  }

  @Override
  public Signer getUnlocked() {
    if (null == this.delegate) {
      throw new HerajException("Unlock account first");
    }
    return this.delegate;
  }

  @Override
  public void setUnlocked(final Signer signer) {
    assertNotNull(signer, "Signer must not null");
    this.delegate = signer;
  }

  @Override
  public boolean lock() {
    if (null == this.delegate) {
      return false;
    }
    this.delegate = null;
    return true;
  }

  @Override
  public boolean isUnlocked() {
    return null != this.delegate;
  }

}
