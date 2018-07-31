/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ModelConverter<DomainModelT, RpcModelT> {
  protected final Function<DomainModelT, RpcModelT> domainConverter;

  protected final Function<RpcModelT, DomainModelT> rpcConverter;

  public RpcModelT convertToRpcModel(DomainModelT domainModel) {
    return domainConverter.apply(domainModel);
  }

  public DomainModelT convertToDomainModel(RpcModelT grpcModel) {
    return rpcConverter.apply(grpcModel);
  }
}
