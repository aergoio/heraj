/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.StringUtils;
import java.net.InetAddress;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@Getter
@ToString
@EqualsAndHashCode
@Builder(builderMethodName = "newBuilder")
public class Peer {

  @NonNull
  protected final InetAddress address;

  @Default
  protected final int port = 0;

  @NonNull
  @Default
  protected final String peerId = StringUtils.EMPTY_STRING;

  @Default
  protected final long bestHeight = 0L;

  @NonNull
  @Default
  protected final BlockHash bestBlockHash = BlockHash.EMPTY;

  @Default
  protected final int state = 0;

  @Default
  protected final boolean hidden = false;

  @Default
  protected final long lashCheck = 0L;

  @Default
  protected final boolean selfPeer = false;

  @NonNull
  @Default
  protected final String version = StringUtils.EMPTY_STRING;

}
