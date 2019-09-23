/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static hera.api.function.Functions.identify;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.Strategy;
import hera.api.function.Function0;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.function.Function3;
import hera.api.function.Function4;
import hera.api.function.Function5;
import hera.api.function.FunctionDecorator;
import hera.api.function.WithIdentity;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;

@ToString
@EqualsAndHashCode
public class StrategyApplier implements FunctionDecorator {

  /**
   * Make {@code StrategyChain} with a strategy in a context.
   *
   * @param context a context
   * @param priorityConfig priority config
   *
   * @return {@code StrategyChain}
   */
  public static StrategyApplier of(final Context context, final PriorityConfig priorityConfig) {
    return new StrategyApplier(context, priorityConfig);
  }

  protected final Logger logger = getLogger(getClass());

  protected final Collection<FunctionDecorator> chain;

  /**
   * Make {@code StrategyChain} based on {@code context}.
   *
   * @param context a context
   * @param priorityConfig priority config
   */
  public StrategyApplier(final Context context, final PriorityConfig priorityConfig) {
    this.chain = new PriorityQueue<>(new Comparator<FunctionDecorator>() {

      protected final Map<Class<? extends Strategy>, Integer> inner =
          priorityConfig.getStrategy2Priority();

      @Override
      public int compare(final FunctionDecorator left, final FunctionDecorator right) {
        final Integer leftPriority = inner.get(left.getClass());
        final Integer rightPriority = inner.get(right.getClass());

        if (null == leftPriority && null == rightPriority) {
          return 0;
        } else if (null == leftPriority) {
          return -1;
        } else if (null == rightPriority) {
          return 1;
        } else {
          return (leftPriority < rightPriority) ? -1 : ((leftPriority == rightPriority) ? 0 : 1);
        }
      }
    });

    for (final Strategy strategy : context.getStrategies()) {
      if (strategy instanceof FunctionDecorator) {
        chain.add((FunctionDecorator) strategy);
      }
    }
    logger.debug("Build strategy chain in order: {}", chain);
  }

  @Override
  public <R> Function0<R> apply(Function0<R> f) {
    Function0<R> ret = f;
    for (final FunctionDecorator next : chain) {
      logger.debug("Apply strategy [{}] to a function", next);
      ret = next.apply(ret);
      if (f instanceof WithIdentity) {
        ret = identify(ret, ((WithIdentity) f).getIdentity());
      }
    }
    return ret;
  }

  @Override
  public <T, R> Function1<T, R> apply(Function1<T, R> f) {
    Function1<T, R> ret = f;
    for (final FunctionDecorator next : chain) {
      logger.debug("Apply strategy [{}] to a function", next);
      ret = next.apply(ret);
      if (f instanceof WithIdentity) {
        ret = identify(ret, ((WithIdentity) f).getIdentity());
      }
    }
    return ret;
  }

  @Override
  public <T1, T2, R> Function2<T1, T2, R> apply(Function2<T1, T2, R> f) {
    Function2<T1, T2, R> ret = f;
    for (final FunctionDecorator next : chain) {
      logger.debug("Apply strategy [{}] to a function", next);
      ret = next.apply(ret);
      if (f instanceof WithIdentity) {
        ret = identify(ret, ((WithIdentity) f).getIdentity());
      }
    }
    return ret;
  }

  @Override
  public <T1, T2, T3, R> Function3<T1, T2, T3, R> apply(Function3<T1, T2, T3, R> f) {
    Function3<T1, T2, T3, R> ret = f;
    for (final FunctionDecorator next : chain) {
      logger.debug("Apply strategy [{}] to a function", next);
      ret = next.apply(ret);
      if (f instanceof WithIdentity) {
        ret = identify(ret, ((WithIdentity) f).getIdentity());
      }
    }
    return ret;
  }

  @Override
  public <T1, T2, T3, T4, R> Function4<T1, T2, T3, T4, R> apply(Function4<T1, T2, T3, T4, R> f) {
    Function4<T1, T2, T3, T4, R> ret = f;
    for (final FunctionDecorator next : chain) {
      logger.debug("Apply strategy [{}] to a function", next);
      ret = next.apply(ret);
      if (f instanceof WithIdentity) {
        ret = identify(ret, ((WithIdentity) f).getIdentity());
      }
    }
    return ret;
  }

  @Override
  public <T1, T2, T3, T4, T5, R> Function5<T1, T2, T3, T4, T5, R> apply(
      Function5<T1, T2, T3, T4, T5, R> f) {
    Function5<T1, T2, T3, T4, T5, R> ret = f;
    for (final FunctionDecorator next : chain) {
      logger.debug("Apply strategy [{}] to a function", next);
      ret = next.apply(ret);
      if (f instanceof WithIdentity) {
        ret = identify(ret, ((WithIdentity) f).getIdentity());
      }
    }
    return ret;
  }

}
