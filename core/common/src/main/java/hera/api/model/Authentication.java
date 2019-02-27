/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
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
   * @param identity an identity
   * @param password a password
   * @return an {@code Authentication} instance
   */
  @ApiAudience.Public
  public static Authentication of(final Identity identity, final String password) {
    return new Authentication(identity, password);
  }

  @Getter
  protected Identity identity;

  @Getter
  protected String password;

  /**
   * Authentication constructor.
   *
   * @param identity an identity
   * @param password a password
   */
  @ApiAudience.Public
  public Authentication(final Identity identity, final String password) {
    assertNotNull(identity, "Identity must not null");
    assertNotNull(password, "Pasasword must not null");
    this.identity = identity;
    this.password = password;
  }

  @Override
  public String toString() {
    return String.format("Authentication(address=%s, password=%s)", identity,
        HexUtils.encode(Sha256Utils.digest(password.getBytes())));
  }

}
