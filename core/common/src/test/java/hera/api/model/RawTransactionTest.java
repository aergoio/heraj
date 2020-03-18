/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import hera.AbstractTestCase;
import hera.api.model.Aer.Unit;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

public class RawTransactionTest extends AbstractTestCase {

  protected final ChainIdHash chainIdHash = ChainIdHash.of(BytesValue.EMPTY);

  protected final AccountAddress accountAddress =
      new AccountAddress("AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5");

  @Test
  public void testCalculateHashWithRawTx() {
    final RawTransaction rawTransaction = RawTransaction.newBuilder(chainIdHash)
        .from(accountAddress)
        .to(accountAddress)
        .amount("10000", Unit.AER)
        .nonce(1L)
        .fee(Fee.of(5))
        .build();
    assertNotNull(rawTransaction.calculateHash());
  }

  @Test
  public void testCalculateHashWithRawTxAndSignature() throws IOException {
    final RawTransaction rawTransaction = RawTransaction.newBuilder(chainIdHash)
        .from(accountAddress)
        .to(accountAddress)
        .amount("10000", Unit.AER)
        .nonce(1L)
        .fee(Fee.of(5))
        .build();
    final TxHash hash = rawTransaction.calculateHash(Signature.EMPTY);
    assertNotNull(hash);
  }

  @Test
  public void testPlainTransaction() {
    final AergoKey aergoKey = new AergoKeyGenerator().create();
    final RawTransaction plainTransaction = RawTransaction.newBuilder()
        .chainIdHash(ChainIdHash.of(BytesValue.EMPTY))
        .from(aergoKey.getAddress())
        .to(aergoKey.getAddress())
        .amount(Aer.AERGO_ONE)
        .nonce(1L)
        .fee(Fee.ZERO)
        .payload(BytesValue.of("payload".getBytes()))
        .build();
    assertNotNull(plainTransaction);
  }

  @Test
  public void testDeployContractTransaction() {
    final AergoKey aergoKey = new AergoKeyGenerator().create();
    final ContractDefinition deployTarget = ContractDefinition.newBuilder()
        .encodedContract(
            "FppTEQaroys1N4P8RcAYYiEhHaQaRE9fzANUx4q2RHDXaRo6TYiTa61n25JcV19grEhpg8qdCWVdsDE2yVfuTKxxcdsTQA2B5zTfxA4GqeRqYGYgWJpj1geuLJAn1RjotdRRxSS1BFA6CAftxjcgiP6WUHacmgtNzoWViYESykhjqVLdmTfV12d44wfh9YAgQ57aRkLNCPkujbnJhdhHEtY1hrJYLCxUDBveqVcDhrrvcHtjDAUcZ5UMzbg6qR1kthGB1Lua6ymw1BmfySNtqb1b6Hp92UPMa7gi5FpAXF5XgpQtEbYDXMbtgu5XtXNhNejrtArcekmjrmPXRoTnMDGUQFcALtnNCrgSv2z5PiXP1coGEbHLTTbxkmJmJz6arEfsb6J1Dv7wnvgysDFVApcpABfwMjHLmnEGvUCLthRfHNBDGydx9jvJQvismqdpDfcEaNBCo5SRMCqGS1FtKtpXjRaHGGFGcTfo9axnsJgAGxLk")
        .amount(Aer.ZERO)
        .constructorArgs(1, 2)
        .build();
    final RawTransaction deployTransaction = RawTransaction.newDeployContractBuilder()
        .chainIdHash(ChainIdHash.of(BytesValue.EMPTY))
        .from(aergoKey.getAddress())
        .definition(deployTarget)
        .nonce(1L)
        .fee(Fee.ZERO)
        .build();
    assertNotNull(deployTransaction);
  }

  @Test
  public void testInvokeContractTransaction() {
    final AergoKey aergoKey = new AergoKeyGenerator().create();
    final ContractInterface contractInterface = dummyContractInterface();
    final ContractInvocation execute = contractInterface.newInvocationBuilder()
        .function("set")
        .args("key", "123")
        .delegateFee(false)
        .build();
    final RawTransaction executeTransaction = RawTransaction.newInvokeContractBuilder()
        .chainIdHash(ChainIdHash.of(BytesValue.EMPTY))
        .from(aergoKey.getAddress())
        .invocation(execute)
        .nonce(1L)
        .fee(Fee.ZERO)
        .build();
    assertNotNull(executeTransaction);
  }

  protected ContractInterface dummyContractInterface() {
    final ContractAddress address =
        ContractAddress.of("AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd");
    final String version = "v1";
    final String language = "lua";
    final ContractFunction set = new ContractFunction("set", false, false, true);
    final ContractFunction get = new ContractFunction("get", false, true, false);
    final List<ContractFunction> functions = asList(set, get);
    final List<StateVariable> stateVariables = emptyList();
    final ContractInterface contractInterface =
        new ContractInterface(address, version, language, functions, stateVariables);
    return contractInterface;
  }

  @Test
  public void testReDeployTransaction() {
    final AergoKey aergoKey = new AergoKeyGenerator().create();
    final ContractDefinition reDeployTarget = ContractDefinition.newBuilder()
        .encodedContract(
            "FppTEQaroys1N4P8RcAYYiEhHaQaRE9fzANUx4q2RHDXaRo6TYiTa61n25JcV19grEhpg8qdCWVdsDE2yVfuTKxxcdsTQA2B5zTfxA4GqeRqYGYgWJpj1geuLJAn1RjotdRRxSS1BFA6CAftxjcgiP6WUHacmgtNzoWViYESykhjqVLdmTfV12d44wfh9YAgQ57aRkLNCPkujbnJhdhHEtY1hrJYLCxUDBveqVcDhrrvcHtjDAUcZ5UMzbg6qR1kthGB1Lua6ymw1BmfySNtqb1b6Hp92UPMa7gi5FpAXF5XgpQtEbYDXMbtgu5XtXNhNejrtArcekmjrmPXRoTnMDGUQFcALtnNCrgSv2z5PiXP1coGEbHLTTbxkmJmJz6arEfsb6J1Dv7wnvgysDFVApcpABfwMjHLmnEGvUCLthRfHNBDGydx9jvJQvismqdpDfcEaNBCo5SRMCqGS1FtKtpXjRaHGGFGcTfo9axnsJgAGxLk")
        .amount(Aer.ZERO)
        .constructorArgs(1, 2)
        .build();
    final RawTransaction reDeployTransaction = RawTransaction.newReDeployContractBuilder()
        .chainIdHash(ChainIdHash.of(BytesValue.EMPTY))
        .creator(aergoKey.getAddress()) // must be creator
        .contractAddress(ContractAddress.of("AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd"))
        .definition(reDeployTarget)
        .nonce(1L)
        .fee(Fee.ZERO)
        .build();
    assertNotNull(reDeployTransaction);
  }

  @Test
  public void testCreateNameTransaction() {
    final AergoKey aergoKey = new AergoKeyGenerator().create();
    final RawTransaction createNameTransaction = RawTransaction.newCreateNameTxBuilder()
        .chainIdHash(ChainIdHash.of(BytesValue.EMPTY))
        .from(aergoKey.getAddress())
        .name("namenamename")
        .nonce(1L)
        .build();
    assertNotNull(aergoKey);
  }

  @Test
  public void testUpdateNameTransaction() {
    final AergoKey aergoKey = new AergoKeyGenerator().create();
    final RawTransaction updateNameTransaction = RawTransaction.newUpdateNameTxBuilder()
        .chainIdHash(ChainIdHash.of(BytesValue.EMPTY))
        .from(aergoKey.getAddress())
        .name("namenamename")
        .nextOwner(AccountAddress.of("AmgVbUZiReUVFXdYb4UVMru4ZqyicSsFPqumRx8LfwMKLFk66SNw"))
        .nonce(1L)
        .build();
    assertNotNull(updateNameTransaction);
  }

  @Test
  public void testStakeTransaction() {
    final AergoKey aergoKey = new AergoKeyGenerator().create();
    final RawTransaction stakeTransaction = RawTransaction.newStakeTxBuilder()
        .chainIdHash(ChainIdHash.of(BytesValue.EMPTY))
        .from(aergoKey.getAddress())
        .amount(Aer.of("10000", Unit.AERGO))
        .nonce(1L)
        .build();
    assertNotNull(stakeTransaction);
  }

  @Test
  public void testUnstakeTransaction() {
    final AergoKey aergoKey = new AergoKeyGenerator().create();
    final RawTransaction unstakeTransaction = RawTransaction.newUnstakeTxBuilder()
        .chainIdHash(ChainIdHash.of(BytesValue.EMPTY))
        .from(aergoKey.getAddress())
        .amount(Aer.of("10000", Unit.AERGO))
        .nonce(1L)
        .build();
    assertNotNull(unstakeTransaction);
  }

  @Test
  public void testVoteTransaction() {
    final AergoKey aergoKey = new AergoKeyGenerator().create();
    final RawTransaction voteTransaction = RawTransaction.newVoteTxBuilder()
        .chainIdHash(ChainIdHash.of(BytesValue.EMPTY))
        .from(aergoKey.getAddress())
        .voteId("voteBP")
        .candidates(asList("123", "456"))
        .nonce(1L)
        .build();
    assertNotNull(voteTransaction);
  }

}
