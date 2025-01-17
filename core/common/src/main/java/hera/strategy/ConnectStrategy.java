/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Strategy;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.HostnameAndPort;


// the ConnectionT is concreate class of io.grpc.ForwardingChannelBuilder2 abstract class, actually.
@ApiAudience.Private
@ApiStability.Unstable
public interface ConnectStrategy<ConnectionT> extends Strategy {

  // Connect to RPC server with default behavior and return channel builder
  ConnectionT connect(HostnameAndPort hostnameAndPort);

  // Connect to RPC server with keepAlive ping, which is same behavior before v1.5.0
  // if keepAliveTime is set to 300 (5 minutes).
  // Setting null to keepAliveTime is same as default method.
  ConnectionT connect(HostnameAndPort hostnameAndPort, Long keepAliveTime);

}
