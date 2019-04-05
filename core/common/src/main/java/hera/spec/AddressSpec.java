/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec;

public class AddressSpec {

  public static final byte PREFIX = 0x42;

  // [odd|even] of publickey.y + [optional 0x00] + publickey.x
  // which is equivalent with s compressed public key (see also X9.62 s 4.2.1)
  public static final int LENGTH = 33;

}
