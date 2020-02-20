/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.exception.RpcConnectionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;

public class AergoClientIT extends AbstractIT {

  @Test
  public void testTryOnUnconnected() {
    final AergoClient client = new AergoClientBuilder()
        .withEndpoint("localhost:9999")
        .withNonBlockingConnect()
        .build();
    try {
      client.getBlockchainOperation().getBlockchainStatus();
      fail();
    } catch (RpcConnectionException e) {
      // good we expected this
    } finally {
      client.close();
    }
  }

  @Test
  public void testContextOnOtherThread() throws Exception {
    final AergoClient client = new AergoClientBuilder().build();
    final ChainIdHash expected = ChainIdHash.of(BytesValue.of(randomUUID().toString().getBytes()));
    client.cacheChainIdHash(expected);
    final ExecutorService executorService = Executors.newSingleThreadExecutor();
    final Future<?> future = executorService.submit(new Runnable() {

      @Override
      public void run() {
        final ChainIdHash actual = client.getCachedChainIdHash();
        assertEquals(expected, actual);
      }
    });
    future.get();
  }

}
