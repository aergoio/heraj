/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class Fee {

  public static final Fee EMPTY = new Fee(null, 0L);

  public static final Fee ZERO = new Fee(Aer.ZERO, 0);

  @Getter
  protected static final Fee defaultFee = new Fee(Aer.GIGA_ONE, 1L);

  /**
   * Build {@code Fee} object.
   *
   * @param limit a fee limit
   * @return created {@code Fee}
   */
  @ApiAudience.Public
  public static Fee of(final long limit) {
    return new Fee(limit);
  }

  @Getter
  protected final Aer price;

  @Getter
  protected final long limit;

  /**
   * Build {@code Fee} object.
   *
   * @param limit a fee limit
   */
  @ApiAudience.Public
  public Fee(final long limit) {
    this(Aer.EMPTY, limit);
  }

  /**
   * Build {@code Fee} object.
   *
   * @param price a fee price
   * @param limit a fee limit
   */
  @ApiAudience.Private
  public Fee(final Aer price, final long limit) {
    this.price = null != price ? price : Aer.EMPTY;
    this.limit = limit >= 0 ? limit : 0L;
  }

}
