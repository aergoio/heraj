/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Context;
import hera.api.SignOperation;
import hera.client.SignTemplate;
import io.grpc.ManagedChannel;

public class RemoteSignStrategy implements SignStrategy<ManagedChannel> {

  @Override
  public SignOperation getSignOperation(final ManagedChannel connection, final Context context) {
    return new SignTemplate(connection, context);
  }

}
