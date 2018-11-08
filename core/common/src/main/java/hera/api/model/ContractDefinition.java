/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.api.encode.Base58WithCheckSum;
import java.util.Arrays;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class ContractDefinition {

  public static final byte PAYLOAD_VERSION = (byte) 0xC0;

  /**
   * Build contract definition.
   *
   * @param encodedContract base58 with checksum encoded contract definition
   * @param args constructor arguments
   *
   * @return created {@code ContractDefinition}
   */
  public static ContractDefinition of(final Base58WithCheckSum encodedContract,
      final Object... args) {
    return new ContractDefinition(encodedContract, args);
  }

  public static ContractDefinitionWithNothing newBuilder() {
    return new ContractDefinition.Builder();
  }

  @Setter
  @Getter
  protected Base58WithCheckSum encodedContract;

  @Setter
  @Getter
  protected Object[] constructorArgs = new Object[0];

  /**
   * Contract definition constructor.
   *
   * @param encodedContract base58 with checksum encoded contract definition
   * @param args constructor arguments
   */
  public ContractDefinition(final Base58WithCheckSum encodedContract, final Object... args) {
    this.encodedContract = encodedContract;
    this.constructorArgs = args;
  }

  @Override
  public boolean equals(Object obj) {
    if (null == obj) {
      return false;
    }
    if (!obj.getClass().equals(getClass())) {
      return false;
    }
    final ContractDefinition other = (ContractDefinition) obj;
    return this.encodedContract.getEncodedValue().equals(other.encodedContract.getEncodedValue())
        && Arrays.equals(this.constructorArgs, other.constructorArgs);
  }

  @Override
  public int hashCode() {
    int hash = 1;
    hash = (31 * hash) + encodedContract.getEncodedValue().hashCode();
    hash = (31 * hash) + constructorArgs.hashCode();
    return hash;
  }

  public interface ContractDefinitionWithNothing {
    ContractDefinitionWithPayload contractInPayload(Base58WithCheckSum contractPayload);
  }

  public interface ContractDefinitionWithPayload extends hera.util.Builder<ContractDefinition> {
    ContractDefinitionWithPayloadAndConstructorArgs constructorArgs(Object... args);
  }

  public interface ContractDefinitionWithPayloadAndConstructorArgs
      extends hera.util.Builder<ContractDefinition> {
  }

  protected static class Builder implements ContractDefinitionWithNothing,
      ContractDefinitionWithPayload, ContractDefinitionWithPayloadAndConstructorArgs {

    protected ContractDefinition contractDefinition = new ContractDefinition();

    @Override
    public ContractDefinitionWithPayload contractInPayload(Base58WithCheckSum contractPayload) {
      contractDefinition.setEncodedContract(contractPayload);
      return this;
    }

    @Override
    public ContractDefinitionWithPayloadAndConstructorArgs constructorArgs(Object... args) {
      contractDefinition.setConstructorArgs(args);
      return this;
    }

    @Override
    public ContractDefinition build() {
      return contractDefinition;
    }

  }

}
