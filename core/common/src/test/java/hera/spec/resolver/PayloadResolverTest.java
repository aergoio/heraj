/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.resolver;

import static hera.api.model.BytesValue.of;
import static hera.util.ValidationUtils.assertNotNull;
import static java.util.UUID.randomUUID;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.StateVariable;
import hera.key.AergoKeyGenerator;
import hera.spec.resolver.PayloadSpec.Type;
import hera.util.Base58Utils;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class PayloadResolverTest extends AbstractTestCase {

  protected final AccountAddress accountAddress =
      new AccountAddress(of(new byte[] {AddressSpec.PREFIX}));

  protected final ContractAddress contractAddress =
      new ContractAddress(of(new byte[] {AddressSpec.PREFIX}));

  @Test
  public void testResolveOnContractDefinition() {
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(
            Base58Utils.encodeWithCheck(new byte[] {ContractDefinitionSpec.PAYLOAD_VERSION}))
        .constructorArgs("1", "2")
        .build();
    final BytesValue payload = PayloadResolver.resolve(Type.ContractDefinition, definition);
    assertNotNull(payload);
  }

  @Test
  public void testResolveOnContractInvocation() {
    final String functionName = randomUUID().toString();
    final List<ContractFunction> functions = new ArrayList<ContractFunction>();
    functions.add(new ContractFunction(functionName));
    final ContractInterface contractInterface =
        new ContractInterface(contractAddress, "", "", functions, new ArrayList<StateVariable>());
    final ContractInvocation invocation =
        contractInterface.newInvocationBuilder().function(functionName).build();
    final BytesValue payload = PayloadResolver.resolve(Type.ContractInvocation, invocation);
    assertNotNull(payload);
  }

  @Test
  public void testResolveOnStake() {
    final BytesValue payload = PayloadResolver.resolve(Type.Stake);
    assertNotNull(payload);
  }

  @Test
  public void testResolveOnUnstake() {
    final BytesValue payload = PayloadResolver.resolve(Type.Unstake);
    assertNotNull(payload);
  }

  @Test
  public void testResolveOnVote() {
    final String voteId = randomUUID().toString();
    final String[] candidates = new String[] {randomUUID().toString(), randomUUID().toString()};
    final BytesValue payload = PayloadResolver.resolve(Type.Vote, voteId, candidates);
    assertNotNull(payload);
  }

  @Test
  public void testResolveOnCreateName() {
    final String name = randomUUID().toString();
    final BytesValue payload = PayloadResolver.resolve(Type.CreateName, name);
    assertNotNull(payload);
  }

  @Test
  public void testResolveOnUpdateName() {
    final String name = randomUUID().toString();
    final AccountAddress nextOwner = new AergoKeyGenerator().create().getAddress();
    final BytesValue payload = PayloadResolver.resolve(Type.UpdateName, name, nextOwner);
    assertNotNull(payload);
  }

}
