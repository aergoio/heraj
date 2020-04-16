/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

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
import hera.exception.RpcException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class AergoClientImpl implements AergoClient {

  protected final ContextStorage<Context> contextStorage;

  protected final ConnectionManager connectionManager;

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
    return chainIdHashHolder.get();
  }

  @Override
  public void cacheChainIdHash(final ChainIdHash chainIdHash) {
    assertNotNull(chainIdHash);
    final Context context = contextStorage.get();
    final ChainIdHashHolder chainIdHashHolder = context.get(GRPC_VALUE_CHAIN_ID_HASH_HOLDER);
    chainIdHashHolder.put(chainIdHash);
  }

  @Override
  public void close() {
    try {
      connectionManager.close();
    } catch (RpcException e) {
      throw e;
    } catch (Exception e) {
      throw new RpcException(e);
    }
  }

}
