/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static hera.util.VersionUtils.trim;
import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hera.api.model.BytesValue;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInvocation;
import hera.api.model.PeerId;
import hera.exception.RpcException;
import hera.util.LittleEndianDataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import org.slf4j.Logger;

public class PayloadResolver {

  public static final String STAKE_PAYLOAD_PREVIX = "s";
  public static final String UNSTAKE_PAYLOAD_PREVIX = "u";
  public static final String VOTE_PAYLOAD_PREVIX = "v";
  public static final String CREATE_NAME_PAYLOAD_PREVIX = "c";
  public static final String UPDATE_NAME_PAYLOAD_PREVIX = "u";

  public enum Type {
    ContractDefinition(ContractDefinition.class),
    ContractInvocation(ContractInvocation.class),
    Stake,
    Unstake,
    Vote(PeerId.class),
    CreateName(String.class),
    UpdateName(String.class, byte[].class);

    protected final Class<?>[] targets;

    private Type(final Class<?>... targets) {
      this.targets = targets;
    }
  }

  protected final Logger logger = getLogger(getClass());

  protected final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Resolve targets in a payload form.
   *
   * @param type a payload type
   * @param targets targets
   * @return resolved payload
   *
   * @throws RpcException if fails
   */
  public BytesValue resolve(final Type type, final Object... targets) {
    logger.trace("Payload resolve type: {}, target size: {}", type, targets.length);
    validate(type, targets);
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
          resolved = resolveStake(targets);
          break;
        case Unstake:
          resolved = resolveUnstake(targets);
          break;
        case CreateName:
          resolved = resolveCreateName(targets);
          break;
        case UpdateName:
          resolved = resolveUpdateName(targets);
          break;
        case Vote:
          resolved = resolveVote(targets);
          break;
        default:
          resolved = of(targets.toString().getBytes());
          break;
      }
    } catch (RpcException e) {
      throw e;
    } catch (Exception e) {
      throw new RpcException(e);
    }
    logger.trace("Resolved payload: {}", resolved);
    return resolved;
  }

  protected void validate(final Type type, final Object[] instances) {
    final int expectedSize = type.targets.length;
    if (instances.length != expectedSize) {
      throw new RpcException("Targets length must be " + expectedSize);
    }
    for (int i = 0; i < expectedSize; ++i) {
      final Class<?> mustbe = type.targets[i];
      if (!mustbe.isInstance(instances[i])) {
        throw new RpcException("Target must be " + mustbe.getName());
      }
    }
  }

  protected BytesValue resolveContractDefinition(Object[] targets) throws IOException {
    final ContractDefinition contractDefinition = (ContractDefinition) targets[0];
    final byte[] rawPayload = trim(contractDefinition.getDecodedContract().getValue());
    final ByteArrayOutputStream rawStream = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream dataOut = new LittleEndianDataOutputStream(rawStream);
    try {
      dataOut.writeInt(rawPayload.length + 4);
      dataOut.write(rawPayload);
      if (!contractDefinition.getConstructorArgs().isEmpty()) {
        final ArrayNode constructorArgs =
            getArgsByJsonArray(contractDefinition.getConstructorArgs());
        dataOut.write(constructorArgs.toString().getBytes());
      }
    } catch (Exception e) {
      throw new RpcException(e);
    } finally {
      dataOut.close();
    }

    return new BytesValue(rawStream.toByteArray());
  }

  protected BytesValue resolveContractInvocation(final Object[] targets) {
    final ContractInvocation contractInvocation = (ContractInvocation) targets[0];
    final ObjectNode node = objectMapper.createObjectNode();
    node.put("Name", contractInvocation.getFunction().getName());
    node.set("Args", getArgsByJsonArray(contractInvocation.getArgs()));
    return new BytesValue(node.toString().getBytes());
  }

  protected ArrayNode getArgsByJsonArray(final List<Object> args) {
    final ArrayNode argsNode = objectMapper.createArrayNode();
    // nil, boolean, number, string, table?
    for (Object arg : args) {
      if (null == arg) {
        argsNode.addNull();
      } else if (arg instanceof Boolean) {
        argsNode.add((Boolean) arg);
      } else if (arg instanceof Integer) {
        argsNode.add((Integer) arg);
      } else if (arg instanceof Long) {
        argsNode.add((Long) arg);
      } else if (arg instanceof Float) {
        argsNode.add((Float) arg);
      } else if (arg instanceof Double) {
        argsNode.add((Double) arg);
      } else if (arg instanceof BigInteger) {
        argsNode.add(new BigDecimal((BigInteger) arg));
      } else if (arg instanceof BigDecimal) {
        argsNode.add((BigDecimal) arg);
      } else if (arg instanceof String) {
        argsNode.add((String) arg);
      } else {
        throw new IllegalArgumentException("Args type must be number or string");
      }
    }
    return argsNode;
  }

  protected BytesValue resolveStake(final Object[] targets) {
    return new BytesValue(STAKE_PAYLOAD_PREVIX.getBytes());
  }

  protected BytesValue resolveUnstake(final Object[] targets) {
    return new BytesValue(UNSTAKE_PAYLOAD_PREVIX.getBytes());
  }

  protected BytesValue resolveCreateName(final Object[] targets) {
    final String name = (String) targets[0];
    return new BytesValue((CREATE_NAME_PAYLOAD_PREVIX + name).getBytes());
  }

  protected BytesValue resolveUpdateName(final Object[] targets) throws IOException {
    final String name = (String) targets[0];
    final byte[] rawAddress = (byte[]) targets[1];
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      os.write(UPDATE_NAME_PAYLOAD_PREVIX.getBytes());
      os.write(name.getBytes());
      os.write(",".getBytes());
      os.write(rawAddress);
    } catch (Exception e) {
      throw new RpcException(e);
    } finally {
      os.close();
    }
    final BytesValue payload = new BytesValue(os.toByteArray());
    return payload;
  }

  protected BytesValue resolveVote(final Object[] targets) throws IOException {
    final PeerId peerId = (PeerId) targets[0];
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      os.write(VOTE_PAYLOAD_PREVIX.getBytes());
      os.write(peerId.getBytesValue().getValue());
    } catch (Exception e) {
      throw new RpcException(e);
    } finally {
      os.close();
    }
    final BytesValue payload = new BytesValue(os.toByteArray());
    return payload;
  }

}
