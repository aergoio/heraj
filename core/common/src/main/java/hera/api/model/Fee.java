/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Fee {

  public static final long MIN_PRICE = 1;

  public static final long MIN_LIMIT = 1;

  @Getter
  protected static Fee defaultFee = new Fee(MIN_PRICE, MIN_LIMIT);

  /**
   * Build {@code Fee} object. If {@code price} < 0, price is set as {@link #MIN_PRICE}. Similarly,
   * if {@code limit} < 0, limit is set as {@link #MIN_LIMIT}.
   *
   * @param price fee price
   * @param limit fee limit
   */
  public static Fee of(final Long price, final long limit) {
    return new Fee(price, limit);
  }

  @Getter
  protected long price;

  @Getter
  protected long limit;

  /**
   * Fee constructor. If {@code price} < 0, price is set as {@link #MIN_PRICE}. Similarly, if
   * {@code limit} < 0, limit is set as {@link #MIN_LIMIT}.
   *
   * @param price fee price
   * @param limit fee limit
   */
  public Fee(final long price, final long limit) {
    this.price = price >= 0 ? price : MIN_PRICE;
    this.limit = limit >= 0 ? limit : MIN_LIMIT;
  }

}
