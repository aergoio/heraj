/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.Getter;
import lombok.Setter;

public class PeerAddress {

  @Getter
  @Setter
  protected BytesValue address;

  @Getter
  @Setter
  protected int port;

  @Getter
  @Setter
  protected byte[] peerId;
}
