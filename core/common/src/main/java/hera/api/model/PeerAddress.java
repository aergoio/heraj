/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class PeerAddress {

  @Getter
  @Setter
  protected BytesValue address;

  @Getter
  @Setter
  protected int port;

  @Getter
  @Setter
  protected BytesValue peerId;
}
