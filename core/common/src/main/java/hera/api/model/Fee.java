/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertTrue;

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

  public static final Fee EMPTY = new Fee(Aer.EMPTY, 0L);

  /**
   * Same as {@link Fee#INFINITY}.
   */
  public static final Fee ZERO = new Fee(Aer.EMPTY, 0L);

  /**
   * Indicates transaction can use fee as much as possible until aergo of fee providers is wasted.
   */
  public static final Fee INFINITY = ZERO;

  /**
   * Use explicit fee amount.
   */
  @Getter
  @Deprecated
  protected static final Fee defaultFee = new Fee(Aer.EMPTY, 10000L);

  /**
   * Build {@code Fee} object.
   *
   * @param limit a gas limit
   * @return created {@code Fee}
   */
  @ApiAudience.Public
  public static Fee of(final long limit) {
    return new Fee(limit);
  }

  // not used currently
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
    assertNotNull(price, "Price must not null");
    assertTrue(limit >= 0, "Limit must be >= 0");
    this.price = price;
    this.limit = limit;
  }

}
