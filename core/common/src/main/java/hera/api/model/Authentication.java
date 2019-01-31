/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.exception.HerajException;
import hera.util.HexUtils;
import hera.util.Sha256Utils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@ApiAudience.Public
@ApiStability.Unstable
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

  @Override
  public String toString() {
    return String.format("Authentication(address=%s, password=%s)", address,
        HexUtils.encode(Sha256Utils.digest(password.getBytes())));
  }

}
