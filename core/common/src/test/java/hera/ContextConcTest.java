/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import hera.api.model.ChainIdHash;
import hera.strategy.FailoverStrategy;
import hera.util.conf.InMemoryConfiguration;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class ContextConcTest extends AbstractTestCase {

  @Test
  public void testScope() {
    final Object[] testParameters =
        new Object[] {ContextProvider.defaultProvider.get(),
            ContextProvider.defaultProvider.get().withScope("test")};

    for (final Object testParameter : testParameters) {
      final Context source = (Context) testParameter;
      final String scope = randomUUID().toString();

      final Context withScope = source.withScope(scope);
      assertEquals(source.getScope(), ((ContextConc) withScope).getScopeParent().getScope());
      assertEquals(scope, withScope.getScope());
      assertEquals(source.getConfiguration(), withScope.getConfiguration());
      assertEquals(source.getStrategies(), withScope.getStrategies());

      final Context withoutScope = withScope.popScope();
      if (source instanceof ContextConc) {
        assertEquals(((ContextConc) source).getScopeParent().getScope(),
            ((ContextConc) withoutScope).getScopeParent().getScope());
      } else {
        assertEquals(source, withoutScope);
      }
      assertEquals(source.getScope(), withoutScope.getScope());
      assertEquals(source.getConfiguration(), withoutScope.getConfiguration());
      assertEquals(source.getStrategies(), withoutScope.getStrategies());
    }
  }

  @Test
  public void testChainIdHash() {
    final Object[] testParameters =
        new Object[] {ContextProvider.defaultProvider.get(),
            ContextProvider.defaultProvider.get().withScope("test")};

    for (final Object testParameter : testParameters) {
      final Context origin = (Context) testParameter;

      final ChainIdHash chainIdHash = new ChainIdHash(of(randomUUID().toString().getBytes()));
      final Context withChainIdHash = origin.withChainIdHash(chainIdHash);
      assertEquals(origin.getScope(), withChainIdHash.getScope());
      assertEquals(origin.getConfiguration(), withChainIdHash.getConfiguration());
      assertEquals(origin.getStrategies(), withChainIdHash.getStrategies());
      assertEquals(chainIdHash, withChainIdHash.getChainIdHash());
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

      final Map<String, Object> map = new HashMap<String, Object>();
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
      final FailoverStrategy strategy = mock(FailoverStrategy.class);

      final Context withStrategy = origin.withStrategy(strategy);
      assertEquals(origin.getScope(), withStrategy.getScope());
      assertNotNull(withStrategy.getStrategy(FailoverStrategy.class));
      assertEquals(origin.getConfiguration(), withStrategy.getConfiguration());

      final Context withoutStrategy = withStrategy.withoutStrategy(strategy.getClass());
      assertEquals(origin.getScope(), withoutStrategy.getScope());
      assertNull(withoutStrategy.getStrategy(FailoverStrategy.class));
      assertEquals(origin.getConfiguration(), withoutStrategy.getConfiguration());
    }
  }

}
