/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.spec.resolver.ContractDefinitionSpec;
import hera.util.EncodingUtils;
import hera.util.VersionUtils;
import java.util.List;
import lombok.NonNull;
import lombok.Value;

@ApiAudience.Public
@ApiStability.Unstable
@Value
public class ContractDefinition {

  @ApiAudience.Public
  public static ContractDefinitionWithNothing newBuilder() {
    return new ContractDefinition.Builder();
  }

  @NonNull
  BytesValue decodedContract;

  @NonNull
  String encodedContract;

  @NonNull
  List<Object> constructorArgs;

  @NonNull
  Aer amount;

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

    protected List<Object> constructorArgs = emptyList();

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
      this.constructorArgs = asList(args);
      return this;
    }

    @Override
    public ContractDefinition build() {
      return new ContractDefinition(encodedContract, constructorArgs, amount);
    }

  }

}
