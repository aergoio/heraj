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
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;

abstract class AbstractMethods {

  protected final transient Logger logger = getLogger(getClass());

  protected AergoRPCServiceBlockingStub getBlockingStub() {
    final Context current = ContextHolder.current();
    final GrpcClient grpcClient = current.get(GRPC_CLIENT);
    logger.trace("GrpcClient: {}", grpcClient);
    return grpcClient.getBlockingStub();
  }

  protected AergoRPCServiceStub getStreamStub() {
    final Context current = ContextHolder.current();
    final GrpcClient grpcClient = current.get(GRPC_CLIENT);
    logger.trace("GrpcClient: {}", grpcClient);
    return grpcClient.getStreamStub();
  }

  protected ChainIdHash getChainIdHash() {
    final Context current = ContextHolder.current();
    final ChainIdHashHolder chainIdHashHolder = current.get(GRPC_VALUE_CHAIN_ID_HASH_HOLDER);
    logger.trace("ChainIdHashHolder: {}", chainIdHashHolder);
    return chainIdHashHolder.get();
  }

}
