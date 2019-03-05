/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static hera.util.VersionUtils.trim;
import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hera.api.model.AccountAddress;
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
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class PayloadResolver {

  public static final String PAYLOAD_VERSION = "v1";

  public static final String STAKE_PAYLOAD_NAME = PAYLOAD_VERSION + "stake";
  public static final String UNSTAKE_PAYLOAD_NAME = PAYLOAD_VERSION + "unstake";
  public static final String VOTE_PAYLOAD_NAME = PAYLOAD_VERSION + "voteBP";
  public static final String CREATE_NAME_PAYLOAD_NAME = PAYLOAD_VERSION + "createName";
  public static final String UPDATE_NAME_PAYLOAD_NAME = PAYLOAD_VERSION + "updateName";

  public enum Type {
    ContractDefinition(ContractDefinition.class),
    ContractInvocation(ContractInvocation.class),
    Vote(PeerId.class),
    Stake,
    Unstake,
    CreateName(String.class),
    UpdateName(String.class, AccountAddress.class);

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
          resolved = resolveStake(targets);
          break;
        case Unstake:
          resolved = resolveUnstake(targets);
          break;
        case Vote:
          resolved = resolveVote(targets);
          break;
        case CreateName:
          resolved = resolveCreateName(targets);
          break;
        case UpdateName:
          resolved = resolveUpdateName(targets);
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

  protected void validateResolveArgs(final Type type, final Object[] instances) {
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

  protected BytesValue resolveContractDefinition(final Object[] targets) throws IOException {
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
    final String name = contractInvocation.getFunction().getName();
    final List<Object> args = contractInvocation.getArgs();
    final ObjectNode node = createJsonNode(name, args);
    return new BytesValue(node.toString().getBytes());
  }

  protected BytesValue resolveStake(final Object[] targets) {
    final ObjectNode node = createJsonNode(STAKE_PAYLOAD_NAME, asList(targets));
    return new BytesValue(node.toString().getBytes());
  }

  protected BytesValue resolveUnstake(final Object[] targets) {
    final ObjectNode node = createJsonNode(UNSTAKE_PAYLOAD_NAME, asList(targets));
    return new BytesValue(node.toString().getBytes());
  }

  protected BytesValue resolveVote(final Object[] targets) throws IOException {
    final List<Object> args = new ArrayList<Object>();
    for (final Object peerId : targets) {
      args.add(((PeerId) peerId).getEncoded());
    }
    final ObjectNode node = createJsonNode(VOTE_PAYLOAD_NAME, args);
    return new BytesValue(node.toString().getBytes());
  }

  protected BytesValue resolveCreateName(final Object[] targets) {
    final ObjectNode node = createJsonNode(CREATE_NAME_PAYLOAD_NAME, asList(targets));
    return new BytesValue(node.toString().getBytes());
  }

  protected BytesValue resolveUpdateName(final Object[] targets) throws IOException {
    final String name = (String) targets[0];
    final String newOwner = ((AccountAddress) targets[1]).getEncoded();
    final List<Object> args = new ArrayList<Object>();
    args.add(name);
    args.add(newOwner);
    final ObjectNode node = createJsonNode(UPDATE_NAME_PAYLOAD_NAME, args);
    return new BytesValue(node.toString().getBytes());
  }

  protected ObjectNode createJsonNode(final String name, final List<Object> args) {
    final ObjectNode node = objectMapper.createObjectNode();
    node.put("Name", name);
    node.set("Args", getArgsByJsonArray(args));
    return node;
  }

  protected ArrayNode getArgsByJsonArray(final List<Object> args) {
    final ArrayNode argsNode = objectMapper.createArrayNode();
    // nil, boolean, number, string, table?
    for (final Object arg : args) {
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

}
