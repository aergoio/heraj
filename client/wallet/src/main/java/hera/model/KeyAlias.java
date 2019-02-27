/*
 * @copyright defined in LICENSE.txt
 */

package hera.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Identity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class KeyAlias implements Identity {

  @Getter
  public String info;

  /**
   * KeyAlias constructor.
   *
   * @param info an alias info
   */
  public KeyAlias(final String info) {
    this.info = info;
  }

}
