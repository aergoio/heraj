/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec;

public class SignatureSpec {

  public static final int HEADER_MAGIC = 0x30;

  public static final int INT_MARKER = 0x02;

  // minimum length of a DER encoded signature which both R and S are 1 byte each.
  // <header-magic> + <1-byte> + <int-marker> + 0x01 + <r.byte> + <int-marker> + 0x01 + <s.byte>
  public static final int MINIMUM_SIGNATURE_LEN   = 8;

}