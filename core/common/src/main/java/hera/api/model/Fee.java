/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.exception.HerajException;
import java.math.BigInteger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Fee {

  public static final BigInteger MIN_PRICE = BigInteger.ONE;

  public static final long MIN_LIMIT = 1;

  @Getter
  protected static Fee defaultFee = new Fee(MIN_PRICE, MIN_LIMIT);

  /**
   * Build {@code Fee} object. If {@code price} &lt; 0, price is set as {@link #MIN_PRICE}.
   * Similarly, if {@code limit} &lt; 0, limit is set as {@link #MIN_LIMIT}.
   *
   * @param price fee price
   * @param limit fee limit
   *
   * @return created {@code Fee}
   */
  public static Fee of(final String price, final long limit) {
    return new Fee(price, limit);
  }


  /**
   * Build {@code Fee} object. If {@code price} &lt; 0, price is set as {@link #MIN_PRICE}.
   * Similarly, if {@code limit} &lt; 0, limit is set as {@link #MIN_LIMIT}.
   *
   * @param price fee price
   * @param limit fee limit
   *
   * @return created {@code Fee}
   */
  public static Fee of(final BigInteger price, final long limit) {
    return new Fee(price, limit);
  }

  @Getter
  protected BigInteger price;

  @Getter
  protected long limit;

  /**
   * Fee constructor. If {@code price} $lt; 0, price is set as {@link #MIN_PRICE}. Similarly, if
   * {@code limit} &lt; 0, limit is set as {@link #MIN_LIMIT}.
   *
   * @param price fee price
   * @param limit fee limit
   */
  public Fee(final String price, final long limit) {
    assertNotNull(price, new HerajException("Price must not null"));
    try {
      BigInteger parsed = new BigInteger(price);
      this.price = parsed.compareTo(BigInteger.ZERO) >= 0 ? parsed : MIN_PRICE;
      this.limit = limit >= 0 ? limit : MIN_LIMIT;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  /**
   * Fee constructor. If {@code price} $lt; 0, price is set as {@link #MIN_PRICE}. Similarly, if
   * {@code limit} &lt; 0, limit is set as {@link #MIN_LIMIT}.
   *
   * @param price fee price
   * @param limit fee limit
   */
  public Fee(final BigInteger price, final long limit) {
    assertNotNull(price);
    this.price = price.compareTo(BigInteger.ZERO) >= 0 ? price : MIN_PRICE;
    this.limit = limit >= 0 ? limit : MIN_LIMIT;
  }

}
