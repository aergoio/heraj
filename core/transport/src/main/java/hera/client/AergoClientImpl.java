
/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_CLIENT;
import static hera.client.ClientContextKeys.GRPC_VALUE_CHAIN_ID_HASH_HOLDER;
import static hera.util.ValidationUtils.assertNotNull;

import hera.Context;
import hera.ContextStorage;
import hera.api.AccountOperation;
import hera.api.BlockOperation;
import hera.api.BlockchainOperation;
import hera.api.ContractOperation;
import hera.api.KeyStoreOperation;
import hera.api.TransactionOperation;
import hera.api.model.ChainIdHash;
import hera.exception.HerajException;
import lombok.Getter;

class AergoClientImpl implements AergoClient {

  protected final ContextStorage<Context> contextStorage;

  AergoClientImpl(final ContextStorage<Context> contextStorage) {
    assertNotNull(contextStorage, "ContextStorage must not null");
    this.contextStorage = contextStorage;
  }

  @Getter(lazy = true)
  private final AccountOperation accountOperation = new AccountTemplate(contextStorage);

  @Getter(lazy = true)
  private final KeyStoreOperation keyStoreOperation = new KeyStoreTemplate(contextStorage);

  @Getter(lazy = true)
  private final BlockOperation blockOperation = new BlockTemplate(contextStorage);

  @Getter(lazy = true)
  private final BlockchainOperation blockchainOperation = new BlockchainTemplate(contextStorage);

  @Getter(lazy = true)
  private final TransactionOperation transactionOperation = new TransactionTemplate(contextStorage);

  @Getter(lazy = true)
  private final ContractOperation contractOperation = new ContractTemplate(contextStorage);

  @Override
  public ChainIdHash getCachedChainIdHash() {
    final Context context = contextStorage.get();
    final ChainIdHashHolder chainIdHashHolder = context.get(GRPC_VALUE_CHAIN_ID_HASH_HOLDER);
    if (null == chainIdHashHolder) {
      throw new HerajException("No chain id hash holder in context");
    }
    return chainIdHashHolder.get();
  }

  @Override
  public void cacheChainIdHash(final ChainIdHash chainIdHash) {
    assertNotNull(chainIdHash);
    final Context context = contextStorage.get();
    final ChainIdHashHolder chainIdHashHolder = context.get(GRPC_VALUE_CHAIN_ID_HASH_HOLDER);
    if (null == chainIdHashHolder) {
      throw new HerajException("No chain id hash holder in context");
    }
    chainIdHashHolder.put(chainIdHash);
  }

  @Override
  public void close() {
    try {
      final Context context = contextStorage.get();
      final GrpcClient grpcClient = context.get(GRPC_CLIENT);
      if (null == grpcClient) {
        throw new HerajException("No grpc client");
      }
      grpcClient.close();
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

}
