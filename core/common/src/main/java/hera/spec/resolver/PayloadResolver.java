/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.resolver;

import static hera.api.model.BytesValue.of;
import static hera.api.model.internal.BytesValueUtils.trimPrefix;
import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInvocation;
import hera.exception.HerajException;
import hera.spec.resolver.PayloadSpec.Type;
import hera.util.LittleEndianDataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public class PayloadResolver {

  protected static final Logger logger = getLogger(PayloadResolver.class);

  /**
   * Resolve targets in a payload form.
   *
   * @param type a payload type
   * @param targets targets
   * @return resolved payload
   *
   * @throws HerajException if fails
   */
  public static BytesValue resolve(final Type type, final Object... targets) {
    try {
      logger.trace("Payload resolve type: {}, target size: {}", type, targets.length);
      validateResolveArgs(type, targets);
      BytesValue resolved = BytesValue.EMPTY;
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
      logger.trace("Resolved payload: {}", resolved);
      return resolved;
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  protected static void validateResolveArgs(final Type type, final Object[] instances) {
    final int expectedSize = type.getTargets().length;
    if (instances.length != expectedSize) {
      throw new IllegalArgumentException("Targets length must be " + expectedSize);
    }
    for (int i = 0; i < expectedSize; ++i) {
      final Class<?> mustbe = type.getTargets()[i];
      final Object instance = instances[i];
      if (!mustbe.isInstance(instance)) {
        throw new IllegalArgumentException(
            "Target must be " + mustbe.getName() + " but was " + instance.getClass().getName());
      }
    }
  }

  protected static BytesValue resolveContractDefinition(final Object[] targets) throws IOException {
    final ContractDefinition contractDefinition = (ContractDefinition) targets[0];
    final byte[] rawPayload = trimPrefix(contractDefinition.getDecodedContract().getValue());
    final ByteArrayOutputStream rawStream = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream dataOut = new LittleEndianDataOutputStream(rawStream);
    try {
      dataOut.writeInt(rawPayload.length + 4);
      dataOut.write(rawPayload);
      if (!contractDefinition.getConstructorArgs().isEmpty()) {
        final String constructorArgs =
            JsonResolver.asJsonArray(contractDefinition.getConstructorArgs());
        dataOut.write(constructorArgs.getBytes());
      }
    } finally {
      dataOut.close();
    }

    return new BytesValue(rawStream.toByteArray());
  }

  protected static BytesValue resolveContractInvocation(final Object[] targets) {
    final ContractInvocation contractInvocation = (ContractInvocation) targets[0];
    final String name = contractInvocation.getFunction().getName();
    final List<Object> args = contractInvocation.getArgs();
    final String jsonForm = asJsonForm(name, args);
    return new BytesValue(jsonForm.getBytes());
  }

  protected static BytesValue resolveStake(final Type type, final Object[] targets) {
    final String jsonForm = asJsonForm(PayloadSpec.VERSION + type.getName(), asList(targets));
    return new BytesValue(jsonForm.getBytes());
  }

  protected static BytesValue resolveUnstake(final Type type, final Object[] targets) {
    final String jsonForm = asJsonForm(PayloadSpec.VERSION + type.getName(), asList(targets));
    return new BytesValue(jsonForm.getBytes());
  }

  protected static BytesValue resolveVote(final Type type, final Object[] targets)
      throws IOException {
    final String voteId = (String) targets[0];
    final String[] candidates = (String[]) targets[1];
    final List<Object> args = new ArrayList<Object>();
    for (final String candidate : candidates) {
      args.add(candidate);
    }
    final String jsonForm = asJsonForm(PayloadSpec.VERSION + voteId, args);
    return new BytesValue(jsonForm.getBytes());
  }

  protected static BytesValue resolveCreateName(final Type type, final Object[] targets) {
    final String jsonForm = asJsonForm(PayloadSpec.VERSION + type.getName(), asList(targets));
    return new BytesValue(jsonForm.getBytes());
  }

  protected static BytesValue resolveUpdateName(final Type type, final Object[] targets)
      throws IOException {
    final String name = (String) targets[0];
    final String newOwner = ((AccountAddress) targets[1]).getEncoded();
    final List<Object> args = new ArrayList<Object>();
    args.add(name);
    args.add(newOwner);
    final String jsonForm = asJsonForm(PayloadSpec.VERSION + type.getName(), args);
    return new BytesValue(jsonForm.toString().getBytes());
  }

  protected static String asJsonForm(final String name, final List<Object> args) {
    final Map<String, Object> map = new HashMap<>();
    map.put("Name", name);
    map.put("Args", args);
    return JsonResolver.asJsonObject(map);
  }

}
