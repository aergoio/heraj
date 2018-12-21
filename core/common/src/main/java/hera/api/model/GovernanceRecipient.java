/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.VersionUtils.envelop;

public class GovernanceRecipient extends AccountAddress {

  public static GovernanceRecipient AERGO_NAME = new GovernanceRecipient("aergo.name");

  private GovernanceRecipient(final String specialName) {
    super(new BytesValue(envelop(specialName.getBytes(), VERSION)));
  }

}
