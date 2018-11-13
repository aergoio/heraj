/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Strategy;
import java.util.concurrent.Future;

public interface TimeoutStrategy extends Strategy {
  <T> T submit(Future<T> future);
}
