/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_CLIENT;
import static hera.client.ClientContextKeys.GRPC_VALUE_CHAIN_ID_HASH_HOLDER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.Context;
import hera.ContextStorage;
import hera.EmptyContext;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import org.junit.Test;

public class AergoClientImplTest extends AbstractTestCase {

  @Test
  public void testGetOperations() {
    final Context context = EmptyContext.getInstance();
    final ContextStorage<Context> contextStorage = new UnmodifiableContextStorage(context);
    final AergoClientImpl aergoClient = new AergoClientImpl(contextStorage);
    assertNotNull(aergoClient.getAccountOperation());
    assertNotNull(aergoClient.getKeyStoreOperation());
    assertNotNull(aergoClient.getBlockOperation());
    assertNotNull(aergoClient.getBlockchainOperation());
    assertNotNull(aergoClient.getTransactionOperation());
    assertNotNull(aergoClient.getContractOperation());
  }

  @Test
  public void testCacheChainIdHash() {
    // given
    final Context context = EmptyContext.getInstance()
        .withValue(GRPC_VALUE_CHAIN_ID_HASH_HOLDER, new ChainIdHashHolder());
    final ContextStorage<Context> contextStorage = new UnmodifiableContextStorage(context);
    final AergoClientImpl aergoClient = new AergoClientImpl(contextStorage);

    // then
    final ChainIdHash expected = ChainIdHash.of(BytesValue.EMPTY);
    aergoClient.cacheChainIdHash(expected);
    final ChainIdHash actual = aergoClient.getCachedChainIdHash();
    assertEquals(expected, actual);
  }

  @Test
  public void testClose() {
    final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, new GrpcClient());
    final ContextStorage<Context> contextStorage = new UnmodifiableContextStorage(context);
    final AergoClientImpl aergoClient = new AergoClientImpl(contextStorage);
    aergoClient.close();
  }

}
