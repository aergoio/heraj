/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import java.net.InetAddress;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Peer {

  @Getter
  @Setter
  protected InetAddress address;

  @Getter
  @Setter
  protected int port;

  @Getter
  @Setter
  protected String peerId;
}
