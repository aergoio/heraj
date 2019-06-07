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
public class ChainStats {

  @Getter
  protected final String report;

  /**
   * VoteInfo constructor.
   * 
   * @param report a report
   */
  public ChainStats(final String report) {
    assertNotNull(report, "Report must not null");
    this.report = report;
  }

}
