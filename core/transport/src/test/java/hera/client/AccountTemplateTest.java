/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.Context;
import hera.ContextStorage;
import hera.EmptyContext;
import hera.Invocation;
import hera.Requester;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.AccountState;
import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.ElectedCandidate;
import hera.api.model.RawTransaction;
import hera.api.model.StakeInfo;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

public class AccountTemplateTest extends AbstractTestCase {

  protected final ContextStorage<Context> contextStorage = new UnmodifiableContextStorage(
      EmptyContext.getInstance());

  @Test
  public void testGetState() throws Exception {
    // given
    final AccountTemplate accountTemplate = new AccountTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final AccountState expected = AccountState.newBuilder().build();
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    accountTemplate.requester = mockRequester;

    // then
    final Account deprecatedOne = new AccountFactory().create(new AergoKeyGenerator().create());
    assertEquals(expected, accountTemplate.getState(deprecatedOne));
    assertEquals(expected, accountTemplate.getState(anyAccountAddress));
  }

  @Test
  public void testCreateNameTx() throws Exception {
    // given
    final AccountTemplate accountTemplate = new AccountTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final TxHash expected = TxHash.of(BytesValue.EMPTY);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    accountTemplate.requester = mockRequester;

    // then
    final Account deprecatedOne = new AccountFactory().create(new AergoKeyGenerator().create());
    assertEquals(expected, accountTemplate.createName(deprecatedOne, anyName, anyNonce));
    assertEquals(expected, accountTemplate.createName(anySigner, anyName, anyNonce));
    assertEquals(expected, accountTemplate.createNameTx(anySigner, anyName, anyNonce));
  }

  @Test
  public void testUpdateNameTx() throws Exception {
    // given
    final AccountTemplate accountTemplate = new AccountTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final TxHash expected = TxHash.of(BytesValue.EMPTY);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    accountTemplate.requester = mockRequester;

    // then
    final Account deprecatedOne = new AccountFactory().create(new AergoKeyGenerator().create());
    assertEquals(expected,
        accountTemplate.updateName(deprecatedOne, anyName, anyAccountAddress, anyNonce));
    assertEquals(expected,
        accountTemplate.updateName(anySigner, anyName, anyAccountAddress, anyNonce));
    assertEquals(expected,
        accountTemplate.updateNameTx(anySigner, anyName, anyAccountAddress, anyNonce));
  }

  @Test
  public void testGetNameOwner() throws Exception {
    // given
    final AccountTemplate accountTemplate = new AccountTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final AccountAddress expected = AccountAddress.EMPTY;
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    accountTemplate.requester = mockRequester;

    // then
    final AccountAddress actual = accountTemplate.getNameOwner(anyName);
    assertEquals(expected, actual);
  }

  @Test
  public void testStakeTx() throws Exception {
    // given
    final AccountTemplate accountTemplate = new AccountTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final TxHash expected = TxHash.of(BytesValue.EMPTY);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    accountTemplate.requester = mockRequester;

    // then
    final Account deprecatedOne = new AccountFactory().create(new AergoKeyGenerator().create());
    assertEquals(expected, accountTemplate.stake(deprecatedOne, anyAmount, anyNonce));
    assertEquals(expected, accountTemplate.stake(anySigner, anyAmount, anyNonce));
    assertEquals(expected, accountTemplate.stakeTx(anySigner, anyAmount, anyNonce));
  }

  @Test
  public void testUnstakeTx() throws Exception {
    // given
    final AccountTemplate accountTemplate = new AccountTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final TxHash expected = TxHash.of(BytesValue.EMPTY);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    accountTemplate.requester = mockRequester;

    // then
    final Account deprecatedOne = new AccountFactory().create(new AergoKeyGenerator().create());
    assertEquals(expected, accountTemplate.unstake(deprecatedOne, anyAmount, anyNonce));
    assertEquals(expected, accountTemplate.unstake(anySigner, anyAmount, anyNonce));
    assertEquals(expected, accountTemplate.unstakeTx(anySigner, anyAmount, anyNonce));
  }

  @Test
  public void testGetStakingInfo() throws Exception {
    // given
    final AccountTemplate accountTemplate = new AccountTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final StakeInfo expected = StakeInfo.newBuilder().build();
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    accountTemplate.requester = mockRequester;

    // then
    final StakeInfo actual = accountTemplate.getStakingInfo(anyAccountAddress);
    assertEquals(expected, actual);
  }

  @Test
  public void testVoteTx() throws Exception {
    // given
    final AccountTemplate accountTemplate = new AccountTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final TxHash expected = TxHash.of(BytesValue.EMPTY);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    accountTemplate.requester = mockRequester;

    // then
    assertEquals(expected, accountTemplate
        .voteTx(anySigner, anyVoteId, anyCandidates, anyNonce));
    assertEquals(expected, accountTemplate
        .vote(anySigner, anyVoteId, anyCandidates, anyNonce));
  }

  @Test
  public void testGetVotesOf() throws Exception {
    // given
    final AccountTemplate accountTemplate = new AccountTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final AccountTotalVote expected = AccountTotalVote.newBuilder().build();
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    accountTemplate.requester = mockRequester;

    // then
    final AccountTotalVote actual = accountTemplate.getVotesOf(anyAccountAddress);
    assertEquals(expected, actual);
  }

  @Test
  public void testListElected() throws Exception {
    // given
    final AccountTemplate accountTemplate = new AccountTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final List<ElectedCandidate> expected = emptyList();
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    accountTemplate.requester = mockRequester;

    // then
    final List<ElectedCandidate> actual = accountTemplate.listElected(anyVoteId, 20);
    assertEquals(expected, actual);
  }

  @Test
  public void testSignAndVerify() {
    final AccountTemplate accountTemplate = new AccountTemplate(contextStorage);
    final AergoKey aergoKey = new AergoKeyGenerator().create();
    final Account account = new AccountFactory().create(aergoKey);
    final RawTransaction rawTransaction = RawTransaction
        .newBuilder(ChainIdHash.of(BytesValue.EMPTY))
        .from(aergoKey.getAddress())
        .to(aergoKey.getAddress())
        .amount(Aer.ZERO)
        .nonce(1L)
        .build();
    final Transaction signed = accountTemplate.sign(account, rawTransaction);
    final boolean result = accountTemplate.verify(account, signed);
    assertTrue(result);
  }

}
