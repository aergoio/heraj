/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class ElectedCandidate {

  @Getter
  protected final String candidateId;

  @Getter
  protected final Aer voted;

  /**
   * Vote constructor.
   *
   * @param candidateId a candidate id
   * @param voted a voted amount
   */
  public ElectedCandidate(final String candidateId, final Aer voted) {
    assertNotNull(candidateId, "Candidate id must not null");
    assertNotNull(voted, "Voted amount must not null");
    this.candidateId = candidateId;
    this.voted = voted;
  }

}
