/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

public class AccountAddress extends BytesValue {

  public static AccountAddress of(final byte[] bytes) {
    return new AccountAddress(bytes);
  }

  public AccountAddress(final byte[] value) {
    super(value);
  }
}
