/*
 * @copyright defined in LICENSE.txt
 */

package hera.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Identity;
import lombok.NonNull;
import lombok.Value;

@ApiAudience.Public
@ApiStability.Unstable
@Value
public class KeyAlias implements Identity {

  @NonNull
  String value;

}
