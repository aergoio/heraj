/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.TxHash;
import hera.key.Signer;

interface TxRequester {

  TxHash request(Signer signer, TxRequestFunction requestFunction) throws Exception;

}
