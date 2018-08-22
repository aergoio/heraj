/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

public class Tuple {

  @Getter
  protected List<Object> values = new ArrayList<>();

  public void apply(Object[] values) {
    this.values = Arrays.asList(values);
  }

  public Object[] unapply() {
    return values.toArray();
  }
  
  @Override
  public String toString() {
    return values.size() > 0 ? values.toString() : "";
  }

}
