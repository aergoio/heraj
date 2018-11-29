/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.strategy.ConnectStrategy;
import hera.util.conf.InMemoryConfiguration;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class ContextConcTest extends AbstractTestCase {

  @Test
  public void testScope() {
    final Object[] testParameters =
        new Object[] {ContextProvider.defaultProvider.get().withScope("test")};

    for (final Object testParameter : testParameters) {
      final Context source = (Context) testParameter;
      final String scope = randomUUID().toString();

      final Context withScope = source.withScope(scope);
      assertEquals(source.getScope(), ((ContextConc) withScope).getScopeParent().getScope());
      assertEquals(scope, withScope.getScope());
      assertEquals(source.getConfiguration(), withScope.getConfiguration());
      assertEquals(source.getStrategies(), withScope.getStrategies());

      final Context withoutScope = withScope.popScope();
      assertEquals(((ContextConc) source).getScopeParent().getScope(),
          ((ContextConc) withoutScope).getScopeParent().getScope());
      assertEquals(source.getScope(), withoutScope.getScope());
      assertEquals(source.getConfiguration(), withoutScope.getConfiguration());
      assertEquals(source.getStrategies(), withoutScope.getStrategies());
    }
  }

  @Test
  public void testConfiguration() {
    final Object[] testParameters =
        new Object[] {ContextProvider.defaultProvider.get(),
            ContextProvider.defaultProvider.get().withScope("test")};

    for (final Object testParameter : testParameters) {
      final Context origin = (Context) testParameter;
      final String key = randomUUID().toString();
      final String value = randomUUID().toString();

      final Map<String, Object> map = new HashMap<>();
      map.put(key, value);
      final InMemoryConfiguration expected = new InMemoryConfiguration(true, map);

      final Context withConfig = origin.withKeyValue(key, value);
      assertEquals(origin.getScope(), withConfig.getScope());
      assertEquals(expected, withConfig.getConfiguration());
      assertEquals(origin.getStrategies(), withConfig.getStrategies());

      final Context withoutConfig = withConfig.withoutKey(key);
      assertEquals(origin.getScope(), withoutConfig.getScope());
      assertEquals(origin.getConfiguration(), withoutConfig.getConfiguration());
      assertEquals(origin.getStrategies(), withoutConfig.getStrategies());
    }
  }

  @Test
  public void testStrategy() {
    final Object[] testParameters =
        new Object[] {ContextProvider.defaultProvider.get(),
            ContextProvider.defaultProvider.get().withScope("test")};

    for (final Object testParameter : testParameters) {
      final Context origin = (Context) testParameter;
      final ConnectStrategy<?> strategy = () -> null;

      final Context withStrategy = origin.withStrategy(strategy);
      assertEquals(origin.getScope(), withStrategy.getScope());
      assertTrue(withStrategy.getStrategy(ConnectStrategy.class).isPresent());
      assertEquals(origin.getConfiguration(), withStrategy.getConfiguration());

      final Context withoutStrategy = withStrategy.withoutStrategy(strategy.getClass());
      assertEquals(origin.getScope(), withoutStrategy.getScope());
      assertTrue(!withoutStrategy.getStrategy(ConnectStrategy.class).isPresent());
      assertEquals(origin.getConfiguration(), withoutStrategy.getConfiguration());
    }
  }

}
