/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.api.model.ChainIdHash;
import java.util.concurrent.atomic.AtomicReference;
import lombok.ToString;

@ToString
class ChainIdHashHolder {

  protected AtomicReference<ChainIdHash> holder = new AtomicReference<>();

  public void put(final ChainIdHash chainIdHash) {
    holder.getAndSet(chainIdHash);
  }

  public ChainIdHash get() {
    return holder.get();
  }

}
