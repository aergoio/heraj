/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl;

import lombok.Getter;
import lombok.Setter;

public class SecuredAccount {

  @Getter
  @Setter
  protected String encodedAddress;

  @Getter
  @Setter
  protected String securedPrivateKey;

  /**
   * Factory method for {@link SecuredAccount}.
   *
   * @param encodedAddress    address
   * @param securedPrivateKey private key
   *
   * @return created account
   */
  public static SecuredAccount of(final String encodedAddress, final String securedPrivateKey) {
    final SecuredAccount securedAccount = new SecuredAccount();
    securedAccount.setEncodedAddress(encodedAddress);
    securedAccount.setSecuredPrivateKey(securedPrivateKey);

    return securedAccount;
  }
}
