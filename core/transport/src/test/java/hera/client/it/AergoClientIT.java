/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;

public class AergoClientIT extends AbstractIT {

  @Test
  public void testContextOnOtherThread() throws Exception {
    final AergoClient client = new AergoClientBuilder()
        .withEndpoint(hostname)
        .build();
    final ExecutorService executorService = Executors.newSingleThreadExecutor();
    final Future<?> future = executorService.submit(new Runnable() {

      @Override
      public void run() {
        client.getBlockchainOperation().getBlockchainStatus();
      }
    });
    future.get();
  }

}
