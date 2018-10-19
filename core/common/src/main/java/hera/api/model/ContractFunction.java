/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.util.StringUtils;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ContractFunction {

  @Getter
  @Setter
  protected String name = StringUtils.EMPTY_STRING;

  @Getter
  @Setter
  protected List<String> argumentNames = Collections.emptyList();
}

