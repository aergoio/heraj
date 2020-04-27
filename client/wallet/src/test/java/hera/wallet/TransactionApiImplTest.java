/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.AccountOperation;
import hera.api.ContractOperation;
import hera.api.TransactionOperation;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractTxHash;
import hera.api.model.Fee;
import hera.api.model.Name;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

public class TransactionApiImplTest extends AbstractTestCase {

  protected final TxRequester txRequester = new TxRequester() {
    @Override
    public TxHash request(Signer signer, TxRequestFunction requestFunction) throws Exception {
      return requestFunction.apply(signer, 1L);
    }
  };

  @Test
  public void testCreateName() {
    // given
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.createNameTx(any(Signer.class), any(Name.class), anyLong()))
        .thenReturn(TxHash.of(BytesValue.EMPTY));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final AergoKey signer = new AergoKeyGenerator().create();
    final TransactionApi transactionApi = new TransactionApiImpl(mockClientProvider, signer,
        txRequester);
    assertNotNull(transactionApi.createName(randomUUID().toString()));
    assertNotNull(transactionApi.createName(anyName));
  }

  @Test
  public void testUpdateName() {
    // given
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation
        .updateNameTx(any(Signer.class), any(Name.class), any(AccountAddress.class), anyLong()))
        .thenReturn(TxHash.of(BytesValue.EMPTY));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final AergoKey signer = new AergoKeyGenerator().create();
    final TransactionApi transactionApi = new TransactionApiImpl(mockClientProvider, signer,
        txRequester);
    assertNotNull(transactionApi.updateName(randomUUID().toString(), anyAccountAddress));
    assertNotNull(transactionApi.updateName(anyName, anyAccountAddress));
  }

  @Test
  public void testStake() {
    // given
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.stakeTx(any(Signer.class), any(Aer.class), anyLong()))
        .thenReturn(TxHash.of(BytesValue.EMPTY));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final AergoKey signer = new AergoKeyGenerator().create();
    final TransactionApi transactionApi = new TransactionApiImpl(mockClientProvider, signer,
        txRequester);
    final TxHash txHash = transactionApi.stake(anyAmount);
    assertNotNull(txHash);
  }

  @Test
  public void testUnstake() {
    // given
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.unstakeTx(any(Signer.class), any(Aer.class), anyLong()))
        .thenReturn(TxHash.of(BytesValue.EMPTY));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final AergoKey signer = new AergoKeyGenerator().create();
    final TransactionApi transactionApi = new TransactionApiImpl(mockClientProvider, signer,
        txRequester);
    final TxHash txHash = transactionApi.unstake(anyAmount);
    assertNotNull(txHash);
  }

  @Test
  public void testVoteBP() {
    // given
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation
        .voteTx(any(Signer.class), anyString(), ArgumentMatchers.<String>anyList(), anyLong()))
        .thenReturn(TxHash.of(BytesValue.EMPTY));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final AergoKey signer = new AergoKeyGenerator().create();
    final TransactionApi transactionApi = new TransactionApiImpl(mockClientProvider, signer,
        txRequester);
    final TxHash txHash = transactionApi.voteBp(anyCandidates);
    assertNotNull(txHash);
  }

  @Test
  public void testVote() {
    // given
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation
        .voteTx(any(Signer.class), anyString(), ArgumentMatchers.<String>anyList(), anyLong()))
        .thenReturn(TxHash.of(BytesValue.EMPTY));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final AergoKey signer = new AergoKeyGenerator().create();
    final TransactionApi transactionApi = new TransactionApiImpl(mockClientProvider, signer,
        txRequester);
    final TxHash txHash = transactionApi.vote(anyVoteId, anyCandidates);
    assertNotNull(txHash);
  }

  @Test
  public void testSendWithAddress() {
    // given
    final TransactionOperation mockOperation = mock(TransactionOperation.class);
    when(mockOperation
        .sendTx(any(Signer.class), any(AccountAddress.class), any(Aer.class), anyLong(),
            any(Fee.class), any(BytesValue.class)))
        .thenReturn(TxHash.of(BytesValue.EMPTY));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getTransactionOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final AergoKey signer = new AergoKeyGenerator().create();
    final TransactionApi transactionApi = new TransactionApiImpl(mockClientProvider, signer,
        txRequester);
    assertNotNull(transactionApi.send(anyAccountAddress, anyAmount, anyFee));
    assertNotNull(transactionApi.send(anyAccountAddress, anyAmount, anyFee, anyPayload));
  }

  @Test
  public void testSendWithName() {
    // given
    final TransactionOperation mockOperation = mock(TransactionOperation.class);
    when(mockOperation
        .sendTx(any(Signer.class), any(Name.class), any(Aer.class), anyLong(),
            any(Fee.class), any(BytesValue.class)))
        .thenReturn(TxHash.of(BytesValue.EMPTY));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getTransactionOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final AergoKey signer = new AergoKeyGenerator().create();
    final TransactionApi transactionApi = new TransactionApiImpl(mockClientProvider, signer,
        txRequester);
    assertNotNull(transactionApi.send(randomUUID().toString(), anyAmount, anyFee));
    assertNotNull(transactionApi.send(randomUUID().toString(), anyAmount, anyFee, anyPayload));
    assertNotNull(transactionApi.send(anyName, anyAmount, anyFee));
    assertNotNull(transactionApi.send(anyName, anyAmount, anyFee, anyPayload));
  }

  @Test
  public void testCommitRawTx() {
    // given
    final TransactionOperation mockOperation = mock(TransactionOperation.class);
    when(mockOperation.commit(any(Transaction.class)))
        .thenReturn(TxHash.of(BytesValue.EMPTY));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getTransactionOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final AergoKey signer = new AergoKeyGenerator().create();
    final TransactionApi transactionApi = new TransactionApiImpl(mockClientProvider, signer,
        txRequester);
    final TxHash txHash = transactionApi.commit(anyRawTransaction);
    assertNotNull(txHash);
  }

  @Test
  public void testCommitTx() {
    // given
    final TransactionOperation mockOperation = mock(TransactionOperation.class);
    when(mockOperation.commit(any(Transaction.class)))
        .thenReturn(TxHash.of(BytesValue.EMPTY));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getTransactionOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final AergoKey signer = new AergoKeyGenerator().create();
    final TransactionApi transactionApi = new TransactionApiImpl(mockClientProvider, signer,
        txRequester);
    final TxHash txHash = transactionApi.commit(anyTransaction);
    assertNotNull(txHash);
  }

  @Test
  public void testDeploy() {
    // given
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation
        .deployTx(any(Signer.class), any(ContractDefinition.class), anyLong(), any(Fee.class)))
        .thenReturn(ContractTxHash.of(BytesValue.EMPTY));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getContractOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final AergoKey signer = new AergoKeyGenerator().create();
    final TransactionApi transactionApi = new TransactionApiImpl(mockClientProvider, signer,
        txRequester);
    final ContractTxHash contractTxHash = transactionApi.deploy(anyDefinition, anyFee);
    assertNotNull(contractTxHash);
  }

  @Test
  public void testRedeploy() {
    // given
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation
        .redeployTx(any(Signer.class), any(ContractAddress.class), any(ContractDefinition.class),
            anyLong(), any(Fee.class)))
        .thenReturn(ContractTxHash.of(BytesValue.EMPTY));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getContractOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final AergoKey signer = new AergoKeyGenerator().create();
    final TransactionApi transactionApi = new TransactionApiImpl(mockClientProvider, signer,
        txRequester);
    final ContractTxHash contractTxHash = transactionApi
        .redeploy(anyContractAddress, anyDefinition, anyFee);
    assertNotNull(contractTxHash);
  }

  @Test
  public void testExecute() {
    // given
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation
        .executeTx(any(Signer.class), any(ContractInvocation.class), anyLong(), any(Fee.class)))
        .thenReturn(ContractTxHash.of(BytesValue.EMPTY));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getContractOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final AergoKey signer = new AergoKeyGenerator().create();
    final TransactionApi transactionApi = new TransactionApiImpl(mockClientProvider, signer,
        txRequester);
    final ContractTxHash contractTxHash = transactionApi.execute(anyInvocation, anyFee);
    assertNotNull(contractTxHash);
  }

}
