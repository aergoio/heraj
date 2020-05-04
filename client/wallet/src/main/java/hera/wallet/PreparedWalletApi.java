package hera.wallet;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface PreparedWalletApi {

  /**
   * Get transaction api.
   *
   * @return a transaction api
   */
  TransactionApi transaction();

  /**
   * Get query api.
   *
   * @return a query api
   */
  QueryApi query();

}
