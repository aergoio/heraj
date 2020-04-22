/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import lombok.RequiredArgsConstructor;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class ModelConverter<DomainModelT, RpcModelT> {
  protected final Function1<DomainModelT, RpcModelT> domainConverter;

  protected final Function1<RpcModelT, DomainModelT> rpcConverter;

  public RpcModelT convertToRpcModel(DomainModelT domainModel) {
    return domainConverter.apply(domainModel);
  }

  public DomainModelT convertToDomainModel(RpcModelT grpcModel) {
    return rpcConverter.apply(grpcModel);
  }
}
