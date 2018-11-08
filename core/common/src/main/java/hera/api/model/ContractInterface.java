/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.api.model.ContractInvocation.ContractInvocationWithInterface;
import hera.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ContractInterface {

  public static final byte PAYLOAD_VERSION = (byte) 0xC0;

  @Getter
  @Setter
  protected ContractAddress contractAddress;

  @Getter
  @Setter
  protected String version = StringUtils.EMPTY_STRING;

  @Getter
  @Setter
  protected String language = StringUtils.EMPTY_STRING;

  @Getter
  @Setter
  protected List<ContractFunction> functions = new ArrayList<ContractFunction>();

  public void addFunction(final ContractFunction function) {
    this.functions.add(function);
  }

  public Optional<ContractFunction> findFunction(final String functionName) {
    return getFunctions().stream().filter(n -> functionName.equals(n.getName())).findFirst();
  }

  public ContractInvocationWithInterface newInvocationBuilder() {
    return new ContractInvocation.Builder().contractInterface(this);
  }

}
