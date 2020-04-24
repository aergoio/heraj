/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.key.AergoKeyGenerator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({
    "javax.crypto.*",
    "javax.management.*",
    "javax.net.ssl.*",
    "javax.security.*",
    "org.bouncycastle.*"})
public abstract class AbstractTestCase {

  protected final transient Logger logger = getLogger(getClass());

  protected final AccountAddress accountAddress = new AergoKeyGenerator().create().getAddress();

  protected final ContractAddress contractAddress =
      new AergoKeyGenerator().create().getAddress().adapt(ContractAddress.class);

  @Before
  public void setUp() {
  }

  protected void runOnOtherThread(final Runnable runnable) {
    try {
      final ExecutorService executorService = Executors.newSingleThreadExecutor();
      final Future<?> future = executorService.submit(runnable);
      future.get();
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    } catch (ExecutionException e) {
      throw new IllegalStateException(e.getCause());
    }
  }

}
