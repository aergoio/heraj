/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Strategy;
import hera.api.AergoEitherApi;

public interface EitherApiStrategy extends Strategy {
  AergoEitherApi getApi();
}
