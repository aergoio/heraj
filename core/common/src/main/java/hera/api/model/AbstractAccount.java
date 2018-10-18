/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(exclude = "mutex")
@EqualsAndHashCode
public abstract class AbstractAccount implements Account {

  protected final Object mutex = this;

  protected long nonce = 1;

  protected long balance = 0;

  @Override
  public void bindState(final AccountState state) {
    synchronized (mutex) {
      setNonce(state.getNonce());
      setBalance(state.getBalance());
    }
  }

  @Override
  public void setNonce(final long nonce) {
    synchronized (mutex) {
      this.nonce = nonce <= 0 ? 1 : nonce;
    }
  }

  @Override
  public long getNonce() {
    synchronized (mutex) {
      return this.nonce;
    }
  }

  @Override
  public long getNonceAndImcrement() {
    synchronized (mutex) {
      return this.nonce++;
    }
  }

  @Override
  public void setBalance(final long balance) {
    synchronized (mutex) {
      this.balance = balance < 0 ? 0 : balance;
    }
  }

  @Override
  public long getBalance() {
    synchronized (mutex) {
      return this.balance;
    }
  }

}
