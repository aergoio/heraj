/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

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
public class Authentication {

  /**
   * Create {@code Authentication} constructor.
   *
   * @param accountAddress an account address
   * @param password a password
   * @return an {@code Authentication} instance
   */
  @ApiAudience.Public
  public static Authentication of(final AccountAddress accountAddress, final String password) {
    return new Authentication(accountAddress, password);
  }

  @Getter
  protected AccountAddress address;

  @Getter
  protected String password;

  /**
   * Authentication constructor.
   *
   * @param accountAddress an account address
   * @param password a password
   */
  @ApiAudience.Public
  public Authentication(final AccountAddress accountAddress, final String password) {
    assertNotNull(accountAddress, new HerajException("Account address must not null"));
    assertNotNull(password, new HerajException("Pasasword must not null"));
    this.address = accountAddress;
    this.password = password;
  }

}
