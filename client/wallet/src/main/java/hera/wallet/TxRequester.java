/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.key.Signer;

interface TxRequester {

  TxHash request(AergoClient aergoClient, Signer signer, TxRequestFunction requestFunction)
      throws Exception;

}
