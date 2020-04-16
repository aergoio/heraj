/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Strategy;
import hera.api.model.HostnameAndPort;

public interface ConnectStrategy<ConnectionT> extends Strategy {
  ConnectionT connect(HostnameAndPort hostnameAndPort);
}
