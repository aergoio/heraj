/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractTupleOrError implements WithError {

  protected Tuple tuple = new Tuple();

  @Setter(value = AccessLevel.PROTECTED)
  @Getter
  protected Throwable error;

  protected void apply(Object[] values) {
    tuple.apply(values);
  }

  protected Object[] unapply() {
    return tuple.unapply();
  }

  @Override
  public String toString() {
    return Optional.ofNullable(error).map(Throwable::toString).orElse(tuple.toString());
  }

  public List<Object> getValues() {
    return tuple.getValues();
  }

}
