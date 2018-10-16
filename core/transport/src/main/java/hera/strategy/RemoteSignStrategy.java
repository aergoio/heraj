/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.api.SignOperation;
import hera.client.SignTemplate;
import io.grpc.ManagedChannel;

public class RemoteSignStrategy implements SignStrategy<ManagedChannel> {

  @Override
  public SignOperation getSignOperation() {
    return new SignTemplate();
  }

}
