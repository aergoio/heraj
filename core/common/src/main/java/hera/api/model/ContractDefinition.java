/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import hera.exception.HerajException;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@ToString
public class ContractDefinition {

  /**
   * Build contract definition.
   *
   * @param encodedContract base58 with checksum encoded contract definition
   * @param args constructor arguments
   *
   * @return created {@code ContractDefinition}
   */
  public static ContractDefinition of(final String encodedContract, Object... args) {
    return new ContractDefinition(encodedContract, args);
  }

  public static final byte PAYLOAD_VERSION = (byte) 0xC0;

  public static ContractDefinitionWithNothing newBuilder() {
    return new ContractDefinition.Builder();
  }

  @Getter
  protected final String encodedContract;

  @Getter
  protected final List<Object> constructorArgs;

  /**
   * Contract definition constructor.
   *
   * @param encodedContract base58 with checksum encoded contract definition
   * @param args constructor arguments
   */
  public ContractDefinition(final String encodedContract, final Object... args) {
    assertNotNull(encodedContract, new HerajException("Encoded contract must not null"));
    this.encodedContract = encodedContract;
    this.constructorArgs =
        null != args ? unmodifiableList(asList(args)) : unmodifiableList(emptyList());
  }

  @Override
  public boolean equals(final Object obj) {
    if (null == obj) {
      return false;
    }
    if (!obj.getClass().equals(getClass())) {
      return false;
    }
    final ContractDefinition other = (ContractDefinition) obj;
    return this.encodedContract.equals(other.encodedContract)
        && this.constructorArgs.equals(other.constructorArgs);
  }

  @Override
  public int hashCode() {
    int hash = 1;
    hash = (31 * hash) + encodedContract.hashCode();
    hash = (31 * hash) + constructorArgs.hashCode();
    return hash;
  }

  public interface ContractDefinitionWithNothing {
    ContractDefinitionWithPayload encodedContract(String encodedContract);
  }

  public interface ContractDefinitionWithPayload extends hera.util.Builder<ContractDefinition> {
    ContractDefinitionWithPayloadAndConstructorArgs constructorArgs(Object... args);
  }

  public interface ContractDefinitionWithPayloadAndConstructorArgs
      extends hera.util.Builder<ContractDefinition> {
  }

  protected static class Builder implements ContractDefinitionWithNothing,
      ContractDefinitionWithPayload, ContractDefinitionWithPayloadAndConstructorArgs {

    protected String encodedContract;

    protected Object[] constructorArgs = new Object[0];

    @Override
    public ContractDefinitionWithPayload encodedContract(final String encodedContract) {
      this.encodedContract = encodedContract;
      return this;
    }

    @Override
    public ContractDefinitionWithPayloadAndConstructorArgs constructorArgs(final Object... args) {
      this.constructorArgs = args;
      return this;
    }

    @Override
    public ContractDefinition build() {
      return new ContractDefinition(this.encodedContract, this.constructorArgs);
    }

  }

}
