/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import java.net.InetAddress;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
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

  @Getter
  @Setter
  protected BlockchainStatus blockchainStatus = new BlockchainStatus();

  // TODO : clarify a state
  @Getter
  @Setter
  protected int state;
}
