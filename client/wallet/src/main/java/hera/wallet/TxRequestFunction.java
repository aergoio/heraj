/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.function.Function2;
import hera.api.model.TxHash;
import hera.key.Signer;

interface TxRequestFunction extends Function2<Signer, Long, TxHash> {

}
