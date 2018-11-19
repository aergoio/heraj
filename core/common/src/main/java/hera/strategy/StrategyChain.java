/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.api.tupleorerror.Function0;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function2;
import hera.api.tupleorerror.Function3;
import hera.api.tupleorerror.Function4;
import hera.api.tupleorerror.FunctionDecorator;
import hera.api.tupleorerror.FunctionDecoratorChain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;

@ToString
@EqualsAndHashCode
public class StrategyChain implements FunctionDecoratorChain {

  /**
   * Make {@code StrategyChain} with a strategy in a context.
   *
   * @see Context
   * @param context a context
   * @return {@code StrategyChain}
   */
  public static StrategyChain of(final Context context) {
    return new StrategyChain(context);
  }

  protected final Logger logger = getLogger(getClass());

  protected final List<FunctionDecorator> chain = new ArrayList<>();

  protected final Iterator<FunctionDecorator> it;

  /**
   * Make {@code StrategyChain} with a strategy in a context.
   *
   * @see Context
   * @param context a context
   */
  public StrategyChain(final Context context) {
    context.listStrategies(FunctionDecorator.class::isInstance).map(FunctionDecorator.class::cast)
        .forEach(chain::add);
    logger.debug("Build strategy chain: {}", chain);
    it = chain.iterator();
  }

  @Override
  public <R> Function0<R> apply(Function0<R> f) {
    if (it.hasNext()) {
      final FunctionDecorator next = it.next();
      logger.debug("Apply strategy [{}] to a function", next);
      return next.applyNext(f, this);
    }
    return f;
  }

  @Override
  public <T, R> Function1<T, R> apply(final Function1<T, R> f) {
    if (it.hasNext()) {
      return it.next().applyNext(f, this);
    }
    return f;
  }

  @Override
  public <T1, T2, R> Function2<T1, T2, R> apply(Function2<T1, T2, R> f) {
    if (it.hasNext()) {
      return it.next().applyNext(f, this);
    }
    return f;
  }

  @Override
  public <T1, T2, T3, R> Function3<T1, T2, T3, R> apply(Function3<T1, T2, T3, R> f) {
    if (it.hasNext()) {
      return it.next().applyNext(f, this);
    }
    return f;
  }

  @Override
  public <T1, T2, T3, T4, R> Function4<T1, T2, T3, T4, R> apply(Function4<T1, T2, T3, T4, R> f) {
    if (it.hasNext()) {
      return it.next().applyNext(f, this);
    }
    return f;
  }

}
