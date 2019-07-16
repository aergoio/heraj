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
import lombok.NonNull;
import lombok.Value;

@ApiAudience.Public
@ApiStability.Unstable
@Value
@Builder(builderMethodName = "newBuilder")
public class Peer {

  @NonNull
  InetAddress address;

  int port;

  @NonNull
  @Default
  String peerId = StringUtils.EMPTY_STRING;

  long bestHeight;

  @NonNull
  @Default
  BlockHash bestBlockHash = BlockHash.of(BytesValue.EMPTY);

  int state;

  boolean hidden;

  long lashCheck;

  boolean selfPeer;

  @NonNull
  @Default
  String version = StringUtils.EMPTY_STRING;

}
