/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Strategy;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.HostnameAndPort;

@ApiAudience.Private
@ApiStability.Unstable
public interface ConnectStrategy<ConnectionT> extends Strategy {

  ConnectionT connect(HostnameAndPort hostnameAndPort);

}
