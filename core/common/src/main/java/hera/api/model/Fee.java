/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.Optional.empty;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Fee {

  public static long MIN_PRICE = 1;

  public static long MIN_LIMIT = 1;

  @Getter
  protected static Fee defaultFee = new Fee(empty(), empty());

  public static Fee of(final Optional<Long> price, final Optional<Long> limit) {
    return new Fee(price, limit);
  }

  @Getter
  protected long price;

  @Getter
  protected long limit;

  public Fee(final Optional<Long> price, final Optional<Long> limit) {
    this.price = price.filter(p -> p >= 0).orElse(MIN_PRICE);
    this.limit = limit.filter(p -> p >= 0).orElse(MIN_LIMIT);
  }

}
