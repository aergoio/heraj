/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_STUB_PROVIDER;
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
    final StubProvider stubProvider = current.get(GRPC_STUB_PROVIDER);
    logger.trace("StubProvider: {}", stubProvider);
    final AergoRPCServiceBlockingStub blockingStub = stubProvider.getBlockingStub();
    return blockingStub;
  }

  protected AergoRPCServiceStub getStreamStub() {
    final Context current = ContextHolder.current();
    final StubProvider stubProvider = current.get(GRPC_STUB_PROVIDER);
    logger.trace("StubProvider: {}", stubProvider);
    final AergoRPCServiceStub streamStub = stubProvider.getStreamStub();
    return streamStub;
  }

  protected ChainIdHash getChainIdHash() {
    final Context current = ContextHolder.current();
    final ChainIdHashHolder chainIdHashHolder = current.get(GRPC_VALUE_CHAIN_ID_HASH_HOLDER);
    logger.trace("ChainIdHashHolder: {}", chainIdHashHolder);
    final ChainIdHash chainIdHash = chainIdHashHolder.get();
    return chainIdHash;
  }

}
