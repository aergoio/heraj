/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_CLIENT;
import static hera.client.ClientContextKeys.GRPC_VALUE_CHAIN_ID_HASH_HOLDER;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.ContextHolder;
import hera.api.model.ChainIdHash;
import hera.exception.HerajException;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;

abstract class AbstractMethods {

  protected final transient Logger logger = getLogger(getClass());

  protected AergoRPCServiceBlockingStub getBlockingStub() {
    final Context current = ContextHolder.current();
    final GrpcClient grpcClient = current.get(GRPC_CLIENT);
    if (null == grpcClient) {
      throw new HerajException("No grpc client in context");
    }
    logger.trace("GrpcClient: {}", grpcClient);
    return grpcClient.getBlockingStub();
  }

  protected AergoRPCServiceStub getStreamStub() {
    final Context current = ContextHolder.current();
    final GrpcClient grpcClient = current.get(GRPC_CLIENT);
    if (null == grpcClient) {
      throw new HerajException("No grpc client in context");
    }
    logger.trace("GrpcClient: {}", grpcClient);
    return grpcClient.getStreamStub();
  }

  protected ChainIdHash getChainIdHash() {
    final Context current = ContextHolder.current();
    final ChainIdHashHolder chainIdHashHolder = current.get(GRPC_VALUE_CHAIN_ID_HASH_HOLDER);
    if (null == chainIdHashHolder) {
      throw new HerajException("No chain id hash holder in context");
    }
    logger.trace("ChainIdHashHolder: {}", chainIdHashHolder);
    return chainIdHashHolder.get();
  }

}
