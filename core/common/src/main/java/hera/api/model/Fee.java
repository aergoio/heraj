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

  public static final Aer MIN_PRICE = Aer.ONE;

  public static final long MIN_LIMIT = 1;

  @Getter
  protected static Fee defaultFee = new Fee(Aer.GIGA_ONE, MIN_LIMIT);

  /**
   * Build {@code Fee} object. If {@code price} is smaller then minimum price, set as
   * {@link #MIN_PRICE}. Similarly, if {@code limit} &lt; 0, limit is set as {@link #MIN_LIMIT}.
   *
   * @param price fee price
   * @param limit fee limit
   * @return created {@code Fee}
   */
  @ApiAudience.Public
  public static Fee of(final Aer price, final long limit) {
    return new Fee(price, limit);
  }

  @Getter
  protected Aer price;

  @Getter
  protected long limit;

  /**
   * Fee constructor. If {@code price} is smaller then minimum price, set as {@link #MIN_PRICE}.
   * Similarly, if {@code limit} &lt; 0, limit is set as {@link #MIN_LIMIT}.
   *
   * @param price fee price
   * @param limit fee limit
   */
  @ApiAudience.Public
  public Fee(final Aer price, final long limit) {
    this.price = null != price ? (price.compareTo(MIN_PRICE) >= 0 ? price : MIN_PRICE) : price;
    this.limit = limit >= 0 ? limit : MIN_LIMIT;
  }

}
