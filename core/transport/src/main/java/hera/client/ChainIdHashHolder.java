/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.api.model.ChainIdHash;
import lombok.ToString;

@ToString
class ChainIdHashHolder {

  protected final Object lock = new Object();
  protected ChainIdHash chainIdHash;

  public void put(final ChainIdHash chainIdHash) {
    synchronized (lock) {
      this.chainIdHash = chainIdHash;
    }
  }

  public ChainIdHash get() {
    return this.chainIdHash;
  }

}
