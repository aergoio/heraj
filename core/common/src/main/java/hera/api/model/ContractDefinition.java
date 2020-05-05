/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.BytesValueUtils.trimPrefix;
import static hera.util.IoUtils.from;
import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Decoder;
import hera.exception.HerajException;
import hera.util.BytesValueUtils;
import java.io.StringReader;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@ApiAudience.Public
@ApiStability.Unstable
@EqualsAndHashCode
public class ContractDefinition implements Payload {

  public static final byte PAYLOAD_VERSION = (byte) 0xC0;

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

  ContractDefinition(final String encodedContract, final List<Object> args,
      final Aer amount) {
    assertNotNull(encodedContract, "Encoded contract must not null");
    assertNotNull(args, "Args must not null");
    assertNotNull(amount, "Amount must not null");
    try {
      final Decoder decoder = Decoder.Base58Check;
      final byte[] raw = from(decoder.decode(new StringReader(encodedContract)));
      final BytesValue withVersion = BytesValue.of(raw);
      if (!hasVersion(withVersion)) {
        throw new HerajException("Encoded contract doesn't have a version");
      }

      this.decodedContract = trimPrefix(withVersion);
      this.encodedContract = encodedContract;
      this.constructorArgs = unmodifiableList(args);
      this.amount = amount;
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  protected boolean hasVersion(final BytesValue bytesValue) {
    return BytesValueUtils.validatePrefix(bytesValue, PAYLOAD_VERSION);
  }

  @Override
  public String toString() {
    return String.format("ContractDefinition(encodedContract=%s, args=%s, amount=%s)",
        encodedContract, constructorArgs, amount);
  }

  public interface ContractDefinitionWithNothing {

    ContractDefinitionWithPayloadReady encodedContract(String encodedContract);
  }

  public interface ContractDefinitionWithPayloadReady
      extends hera.util.Builder<ContractDefinition> {

    ContractDefinitionWithPayloadReady constructorArgs(List<Object> args);

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
    public ContractDefinitionWithPayloadReady constructorArgs(final List<Object> args) {
      if (null != args) {
        this.constructorArgs = args;
      }
      return this;
    }

    @Override
    public ContractDefinitionWithPayloadReady constructorArgs(final Object... args) {
      if (null != args) {
        this.constructorArgs = asList(args);
      }
      return this;
    }

    @Override
    public ContractDefinition build() {
      return new ContractDefinition(encodedContract, constructorArgs, amount);
    }

  }

}
