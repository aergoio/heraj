/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ContractCall {

  public static ContractCall.Builder newBuilder() {
    return new ContractCall.Builder();
  }

  @Getter
  protected final ContractAddress address;

  @Getter
  protected final ContractFunction function;

  @Getter
  protected List<Object> args = Collections.emptyList();

  public static class Builder {

    protected ContractAddress address;

    protected ContractFunction function;

    protected final List<Object> args = new ArrayList<Object>();

    public ContractCall.Builder setAddress(final ContractAddress contractAddress) {
      this.address = contractAddress;
      return this;
    }

    public ContractCall.Builder setFunction(final ContractFunction contractFunction) {
      this.function = contractFunction;
      return this;
    }

    public ContractCall.Builder setArgs(final Object... args) {
      this.args.addAll(Arrays.asList(args));
      return this;
    }

    public ContractCall build() {
      return new ContractCall(this.address, this.function, this.args);
    }

  }

}
