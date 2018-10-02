/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Strategy;
import hera.api.AergoApi;

public interface ApiStrategy extends Strategy {
  AergoApi getApi();
}
