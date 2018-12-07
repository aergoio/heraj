/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertTrue;

import hera.exception.HerajException;
import java.math.BigInteger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class AccountState {

  @Getter
  protected final AccountAddress address;

  @Getter
  protected final long nonce;

  @Getter
  protected final BigInteger balance;

  /**
   * AccountState constructor.
   *
   * @param address an account address
   * @param nonce a nonce. Must be &gt;= 0
   * @param balance a balance. Must be &gt;= 0
   */
  public AccountState(final AccountAddress address, final long nonce, final BigInteger balance) {
    assertNotNull(address, new HerajException("Account address must not null"));
    assertTrue(nonce >= 0, new HerajException("Nonce must >= 0"));
    assertNotNull(balance, new HerajException("Account balance must not null"));
    assertTrue(balance.compareTo(BigInteger.ZERO) >= 0, new HerajException("Balance must >= 0"));
    this.address = address;
    this.nonce = nonce;
    this.balance = balance;
  }

}
