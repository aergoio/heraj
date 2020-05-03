/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.TxHash;
import hera.key.Signer;

@ApiAudience.Private
@ApiStability.Unstable
public interface TxRequester {

  TxHash request(AergoClient aergoClient, Signer signer, TxRequestFunction requestFunction)
      throws Exception;

}
