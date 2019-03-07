/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.resolver;

import static hera.api.model.BytesValue.of;
import static hera.util.VersionUtils.trim;
import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInvocation;
import hera.api.model.PeerId;
import hera.exception.HerajException;
import hera.spec.PayloadSpec.Type;
import hera.util.LittleEndianDataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class PayloadResolver {

  public static final String JSON_START = "{ ";
  public static final String JSON_END = " }";
  public static final String JSON_ARRAY_START = "[ ";
  public static final String JSON_ARRAY_END = " ]";
  public static final String JSON_NEXT = ",";

  protected final Logger logger = getLogger(getClass());

  /**
   * Resolve targets in a payload form.
   *
   * @param type a payload type
   * @param targets targets
   * @return resolved payload
   *
   * @throws HerajException if fails
   */
  public BytesValue resolve(final Type type, final Object... targets) {
    logger.trace("Payload resolve type: {}, target size: {}", type, targets.length);
    validateResolveArgs(type, targets);
    BytesValue resolved = BytesValue.EMPTY;
    try {
      switch (type) {
        case ContractDefinition:
          resolved = resolveContractDefinition(targets);
          break;
        case ContractInvocation:
          resolved = resolveContractInvocation(targets);
          break;
        case Stake:
          resolved = resolveStake(type, targets);
          break;
        case Unstake:
          resolved = resolveUnstake(type, targets);
          break;
        case Vote:
          resolved = resolveVote(type, targets);
          break;
        case CreateName:
          resolved = resolveCreateName(type, targets);
          break;
        case UpdateName:
          resolved = resolveUpdateName(type, targets);
          break;
        default:
          resolved = of(targets.toString().getBytes());
          break;
      }
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
    logger.trace("Resolved payload: {}", resolved);
    return resolved;
  }

  protected void validateResolveArgs(final Type type, final Object[] instances) {
    final int expectedSize = type.getTargets().length;
    if (instances.length != expectedSize) {
      throw new HerajException("Targets length must be " + expectedSize);
    }
    for (int i = 0; i < expectedSize; ++i) {
      final Class<?> mustbe = type.getTargets()[i];
      if (!mustbe.isInstance(instances[i])) {
        throw new HerajException("Target must be " + mustbe.getName());
      }
    }
  }

  protected BytesValue resolveContractDefinition(final Object[] targets) throws IOException {
    final ContractDefinition contractDefinition = (ContractDefinition) targets[0];
    final byte[] rawPayload = trim(contractDefinition.getDecodedContract().getValue());
    final ByteArrayOutputStream rawStream = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream dataOut = new LittleEndianDataOutputStream(rawStream);
    try {
      dataOut.writeInt(rawPayload.length + 4);
      dataOut.write(rawPayload);
      if (!contractDefinition.getConstructorArgs().isEmpty()) {
        final String constructorArgs = asJsonArray(contractDefinition.getConstructorArgs());
        dataOut.write(constructorArgs.getBytes());
      }
    } catch (Exception e) {
      throw new HerajException(e);
    } finally {
      dataOut.close();
    }

    return new BytesValue(rawStream.toByteArray());
  }

  protected BytesValue resolveContractInvocation(final Object[] targets) {
    final ContractInvocation contractInvocation = (ContractInvocation) targets[0];
    final String name = contractInvocation.getFunction().getName();
    final List<Object> args = contractInvocation.getArgs();
    final String jsonForm = asJsonForm(name, args);
    return new BytesValue(jsonForm.getBytes());
  }

  protected BytesValue resolveStake(final Type type, final Object[] targets) {
    final String jsonForm = asJsonForm(type.getName(), asList(targets));
    return new BytesValue(jsonForm.getBytes());
  }

  protected BytesValue resolveUnstake(final Type type, final Object[] targets) {
    final String jsonForm = asJsonForm(type.getName(), asList(targets));
    return new BytesValue(jsonForm.getBytes());
  }

  protected BytesValue resolveVote(final Type type, final Object[] targets) throws IOException {
    final List<Object> args = new ArrayList<Object>();
    for (final Object peerId : targets) {
      args.add(((PeerId) peerId).getEncoded());
    }
    final String jsonForm = asJsonForm(type.getName(), args);
    return new BytesValue(jsonForm.getBytes());
  }

  protected BytesValue resolveCreateName(final Type type, final Object[] targets) {
    final String jsonForm = asJsonForm(type.getName(), asList(targets));
    return new BytesValue(jsonForm.getBytes());
  }

  protected BytesValue resolveUpdateName(final Type type, final Object[] targets)
      throws IOException {
    final String name = (String) targets[0];
    final String newOwner = ((AccountAddress) targets[1]).getEncoded();
    final List<Object> args = new ArrayList<Object>();
    args.add(name);
    args.add(newOwner);
    final String jsonForm = asJsonForm(type.getName(), args);
    return new BytesValue(jsonForm.toString().getBytes());
  }

  protected String asJsonForm(final String name, final List<Object> args) {
    final StringBuilder sb = new StringBuilder();
    sb.append(JSON_START);
    sb.append(keyAndValue("Name", asJsonString(name)));
    if (!args.isEmpty()) {
      sb.append(JSON_NEXT);
      sb.append(keyAndValue("Args", asJsonArray(args)));
    }
    sb.append(JSON_END);
    return sb.toString();
  }

  protected String asJsonArray(final List<Object> args) {
    final StringBuilder sb = new StringBuilder();
    sb.append(JSON_ARRAY_START);
    // nil, boolean, number, string, table?
    for (int i = 0; i < args.size(); ++i) {
      if (i != 0) {
        sb.append(JSON_NEXT);
      }

      final Object arg = args.get(i);
      if (null == arg) {
        sb.append(asJsonNull());
      } else if (arg instanceof Number) {
        sb.append(asJsonNumber((Number) arg));
      } else if (arg instanceof BigInteger) {
        sb.append(asJsonNumber((BigInteger) arg));
      } else if (arg instanceof BigDecimal) {
        sb.append(asJsonNumber((BigDecimal) arg));
      } else if (arg instanceof String) {
        sb.append(asJsonString((String) arg));
      } else if (arg instanceof Boolean) {
        sb.append(asJsonBoolean((Boolean) arg));
      } else {
        throw new IllegalArgumentException("Args type must be number or string");
      }
    }
    sb.append(JSON_ARRAY_END);
    return sb.toString();
  }

  protected String keyAndValue(final String key, final String value) {
    return "\"" + key + "\"" + ":" + value;
  }

  protected String asJsonString(final String target) {
    return "\"" + target + "\"";
  }

  protected String asJsonNumber(final Number target) {
    return target.toString();
  }

  protected String asJsonNumber(final BigInteger target) {
    return target.toString(10);
  }

  protected String asJsonNumber(final BigDecimal target) {
    return target.toString();
  }

  protected String asJsonNull() {
    return "null";
  }

  protected String asJsonBoolean(final Boolean target) {
    return target.toString();
  }

}
