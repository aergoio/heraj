/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.util.StringUtils;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class AbiSet {

  @Getter
  @Setter
  protected String version = StringUtils.EMPTY_STRING;

  @Getter
  @Setter
  protected String language = StringUtils.EMPTY_STRING;

  @Getter
  @Setter
  protected List<Abi> abis = Collections.emptyList();
}
