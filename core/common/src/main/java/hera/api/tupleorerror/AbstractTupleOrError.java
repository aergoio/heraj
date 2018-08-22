/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractTupleOrError implements WithError {

  @Getter
  protected Tuple tuple = new Tuple();

  @Setter(value = AccessLevel.PROTECTED)
  @Getter
  protected Throwable error;

  public void apply(Object[] values) {
    tuple.apply(values);
  }

  public Object[] unapply() {
    return tuple.unapply();
  }

  @Override
  public String toString() {
    return Optional.ofNullable(error).map(Throwable::toString).orElse(tuple.toString());
  }

}
