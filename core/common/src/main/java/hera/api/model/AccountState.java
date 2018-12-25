/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertTrue;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.exception.HerajException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class AccountState {

  @Getter
  protected final AccountAddress address;

  @Getter
  protected final long nonce;

  @Getter
  protected final Aer balance;

  /**
   * AccountState constructor.
   *
   * @param address an account address
   * @param nonce a nonce.
   * @param balance a balance. Must be &gt;= 0
   */
  @ApiAudience.Private
  public AccountState(final AccountAddress address, final long nonce, final Aer balance) {
    assertNotNull(address, new HerajException("Account address must not null"));
    assertTrue(nonce >= 0, new HerajException("Nonce must >= 0"));
    assertNotNull(balance, new HerajException("Account balance must not null"));
    this.address = address;
    this.nonce = nonce;
    this.balance = balance;
  }

}
