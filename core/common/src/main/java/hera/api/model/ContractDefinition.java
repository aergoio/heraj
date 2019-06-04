/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.exception.DecodingFailureException;
import hera.exception.InvalidVersionException;
import hera.spec.ContractDefinitionSpec;
import hera.util.EncodingUtils;
import hera.util.VersionUtils;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
public class ContractDefinition {

  @ApiAudience.Public
  public static ContractDefinitionWithNothing newBuilder() {
    return new ContractDefinition.Builder();
  }

  @Getter
  protected final BytesValue decodedContract;

  @Getter
  protected final String encodedContract;

  @Getter
  protected final List<Object> constructorArgs;

  @Getter
  protected final Aer amount;

  /**
   * Contract definition constructor.
   *
   * @param encodedContract base58 with checksum encoded contract definition
   * @param args constructor arguments
   * @param amount an aergo amount
   *
   * @throws DecodingFailureException if decoding failure
   * @throws InvalidVersionException if encodedContract version mismatches
   */
  ContractDefinition(final String encodedContract, final List<Object> args,
      final Aer amount) {
    assertNotNull(encodedContract, "Encoded contract must not null");
    assertNotNull(args, "Args must not null");
    assertNotNull(amount, "Amount must not null");
    final BytesValue decodedContract = EncodingUtils.decodeBase58WithCheck(encodedContract);
    VersionUtils.validate(decodedContract, ContractDefinitionSpec.PAYLOAD_VERSION);

    this.decodedContract = decodedContract;
    this.encodedContract = encodedContract;
    this.constructorArgs = unmodifiableList(args);
    this.amount = amount;
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
        && this.constructorArgs.equals(other.constructorArgs)
        && this.amount.equals(other.amount);
  }

  @Override
  public int hashCode() {
    int hash = 1;
    hash = (31 * hash) + encodedContract.hashCode();
    hash = (31 * hash) + amount.hashCode();
    hash = (31 * hash) + constructorArgs.hashCode();
    return hash;
  }

  public interface ContractDefinitionWithNothing {
    ContractDefinitionWithPayloadReady encodedContract(String encodedContract);
  }

  public interface ContractDefinitionWithPayloadReady
      extends hera.util.Builder<ContractDefinition> {
    ContractDefinitionWithPayloadReady constructorArgs(Object... args);

    ContractDefinitionWithPayloadReady amount(Aer amount);
  }

  protected static class Builder
      implements ContractDefinitionWithNothing, ContractDefinitionWithPayloadReady {

    protected String encodedContract;

    protected Object[] constructorArgs = new Object[0];

    protected Aer amount = Aer.EMPTY;

    @Override
    public ContractDefinitionWithPayloadReady encodedContract(final String encodedContract) {
      this.encodedContract = encodedContract;
      return this;
    }

    @Override
    public ContractDefinitionWithPayloadReady amount(final Aer amount) {
      this.amount = amount;
      return this;
    }

    @Override
    public ContractDefinitionWithPayloadReady constructorArgs(final Object... args) {
      this.constructorArgs = args;
      return this;
    }

    @Override
    public ContractDefinition build() {
      return new ContractDefinition(encodedContract, asList(constructorArgs), amount);
    }

  }

}
