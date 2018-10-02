/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Strategy;
import hera.api.AergoAsyncApi;

public interface AsyncApiStrategy extends Strategy {
  AergoAsyncApi getApi();
}
