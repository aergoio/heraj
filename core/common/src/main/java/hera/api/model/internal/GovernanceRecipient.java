/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import static hera.util.VersionUtils.envelop;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.spec.resolver.AddressSpec;

@ApiAudience.Private
@ApiStability.Unstable
public class GovernanceRecipient extends AccountAddress {

  public static GovernanceRecipient AERGO_SYSTEM = new GovernanceRecipient("aergo.system");

  public static GovernanceRecipient AERGO_NAME = new GovernanceRecipient("aergo.name");

  private GovernanceRecipient(final String specialName) {
    super(new BytesValue(envelop(specialName.getBytes(), AddressSpec.PREFIX)));
  }

}
