/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import hera.api.model.ChainIdHash;
import hera.strategy.FailoverStrategy;
import hera.util.conf.InMemoryConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;

public class ThreadLocalContextTest extends AbstractTestCase {

  @Test
  public void testScope() throws Exception {
    final int count = 2;
    final ExecutorService executorService = Executors.newFixedThreadPool(2);

    final List<Future<?>> tasks = new ArrayList<Future<?>>();
    for (int i = 0; i < count; ++i) {
      tasks.add(executorService.submit(new Runnable() {

        @Override
        public void run() {
          final ContextProvider provider =
              new ThreadLocalContextProvider(EmptyContext.getInstance(), this);
          final Context expected = provider.get()
              .withScope(randomUUID().toString())
              .withScope(randomUUID().toString())
              .popScope();

          final Context actual = provider.get();
          assertEquals(expected, actual);
        }
      }));
    }
    for (final Future<?> task : tasks) {
      task.get();
    }

    assertEquals(new ThreadLocalContext(this), new ThreadLocalContextProvider(null, this).get());
  }

  @Test
  public void testChainIdHash() throws Exception {
    final int count = 2;
    final ExecutorService executorService = Executors.newFixedThreadPool(2);

    final List<Future<?>> tasks = new ArrayList<Future<?>>();
    for (int i = 0; i < count; ++i) {
      tasks.add(executorService.submit(new Runnable() {

        @Override
        public void run() {
          final ContextProvider provider =
              new ThreadLocalContextProvider(EmptyContext.getInstance(), this);
          final Context expected = provider.get()
              .withChainIdHash(new ChainIdHash(of(randomUUID().toString().getBytes())))
              .withChainIdHash(new ChainIdHash(of(randomUUID().toString().getBytes())));

          final Context actual = provider.get();
          assertEquals(expected, actual);
        }
      }));
    }
    for (final Future<?> task : tasks) {
      task.get();
    }

    assertEquals(new ThreadLocalContext(this),
        new ThreadLocalContextProvider(EmptyContext.getInstance(), this).get());
  }

  @Test
  public void testConfiguration() throws Exception {
    final int count = 2;
    final ExecutorService executorService = Executors.newFixedThreadPool(2);

    final List<Future<?>> tasks = new ArrayList<Future<?>>();
    for (int i = 0; i < count; ++i) {
      tasks.add(executorService.submit(new Runnable() {

        @Override
        public void run() {
          final ContextProvider provider =
              new ThreadLocalContextProvider(EmptyContext.getInstance(), this);

          final String key = randomUUID().toString();
          final String value = randomUUID().toString();
          final Map<String, Object> map = new HashMap<String, Object>();
          map.put(key, value);
          final InMemoryConfiguration configuration = new InMemoryConfiguration(true, map);

          final Context expected = provider.get()
              .withConfiguration(configuration);

          final Context actual = provider.get();
          assertEquals(expected, actual);
        }
      }));
    }
    for (final Future<?> task : tasks) {
      task.get();
    }

    assertEquals(new ThreadLocalContext(this),
        new ThreadLocalContextProvider(EmptyContext.getInstance(), this).get());
  }

  @Test
  public void testStrategy() throws Exception {
    final int count = 2;
    final ExecutorService executorService = Executors.newFixedThreadPool(2);

    final List<Future<?>> tasks = new ArrayList<Future<?>>();
    for (int i = 0; i < count; ++i) {
      tasks.add(executorService.submit(new Runnable() {

        @Override
        public void run() {
          final ContextProvider provider =
              new ThreadLocalContextProvider(EmptyContext.getInstance(), this);

          final FailoverStrategy strategy = mock(FailoverStrategy.class);
          final Context expected = provider.get()
              .withStrategy(strategy);

          final Context actual = provider.get();
          assertEquals(expected, actual);
        }
      }));
    }
    for (final Future<?> task : tasks) {
      task.get();
    }

    assertEquals(new ThreadLocalContext(this),
        new ThreadLocalContextProvider(EmptyContext.getInstance(), this).get());
  }

}
