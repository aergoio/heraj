package hera.wallet;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface PreparedWalletApi {

  TransactionApi transaction();

  QueryApi query();

}
