/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.util.pki.ECDSAKeyGenerator;

public class AergoKeyGenerator {

  public AergoKey create() throws Exception {
    return new AergoKey(new ECDSAKeyGenerator().create());
  }

}
