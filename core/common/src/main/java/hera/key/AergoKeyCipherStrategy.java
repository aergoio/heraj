/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Private
@ApiStability.Unstable
public interface AergoKeyCipherStrategy<T> {

  T encrypt(AergoKey aergoKey, String passphrase);

  AergoKey decrypt(T encrypted, String passphrase);

}
