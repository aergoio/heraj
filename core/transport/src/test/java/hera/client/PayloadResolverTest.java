/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static hera.util.ValidationUtils.assertNotNull;
import static java.util.UUID.randomUUID;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInvocation;
import hera.api.model.PeerId;
import hera.client.PayloadResolver.Type;
import hera.util.Base58Utils;
import org.junit.Test;

public class PayloadResolverTest extends AbstractTestCase {

  protected final AccountAddress accountAddress =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected final ContractAddress contractAddress =
      new ContractAddress(of(new byte[] {AccountAddress.VERSION}));

  protected final PayloadResolver resolver = new PayloadResolver();

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testResolveOnContractDefinition() {
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(
            Base58Utils.encodeWithCheck(new byte[] {ContractDefinition.PAYLOAD_VERSION}))
        .constructorArgs("1", "2")
        .build();
    final BytesValue payload = resolver.resolve(Type.ContractDefinition, definition);
    assertNotNull(payload);
  }

  @Test
  public void testResolveOnContractInvocation() {
    final ContractInvocation invocation = new ContractInvocation(contractAddress,
        new ContractFunction(randomUUID().toString()));
    final BytesValue payload = resolver.resolve(Type.ContractInvocation, invocation);
    assertNotNull(payload);
  }

  @Test
  public void testResolveOnStake() {
    final BytesValue payload = resolver.resolve(Type.Stake);
    assertNotNull(payload);
  }

  @Test
  public void testResolveOnUnstake() {
    final BytesValue payload = resolver.resolve(Type.Unstake);
    assertNotNull(payload);
  }

  @Test
  public void testResolveOnVote() {
    final PeerId peerId = new PeerId(of(randomUUID().toString().getBytes()));
    final BytesValue payload = resolver.resolve(Type.Vote, peerId);
    assertNotNull(payload);
  }

  @Test
  public void testResolveOnCreateName() {
    final String name = randomUUID().toString();
    final BytesValue payload = resolver.resolve(Type.CreateName, name);
    assertNotNull(payload);
  }

  @Test
  public void testResolveOnUpdateName() {
    final String name = randomUUID().toString();
    final byte[] nextOwner = randomUUID().toString().getBytes();
    final BytesValue payload = resolver.resolve(Type.UpdateName, name, nextOwner);
    assertNotNull(payload);
  }

}
