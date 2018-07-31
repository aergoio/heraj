/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.protobuf.ByteString.copyFrom;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.transport.ModelConverter;
import java.util.Optional;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.Blockchain;
import types.Blockchain.TxBody;
import types.Rpc;
import types.Rpc.CommitResult;
import types.Rpc.VerifyResult;

@PrepareForTest({AergoRPCServiceBlockingStub.class, Blockchain.Tx.class, VerifyResult.class,
    CommitResult.class})
public class TransactionTemplateTest extends AbstractTestCase {

  protected final byte[] TXHASH = randomUUID().toString().getBytes();

  protected static final ModelConverter<Transaction, Blockchain.Tx> converter = mock(
      ModelConverter.class);

  @BeforeClass
  public static void setUpBeforeClass() {
    when(converter.convertToRpcModel(any(Transaction.class))).thenReturn(mock(Blockchain.Tx.class));
    when(converter.convertToDomainModel(any(Blockchain.Tx.class)))
        .thenReturn(mock(Transaction.class));
  }

  @Test
  public void testGetTransaction() {
    final AergoRPCServiceBlockingStub aergoService = mock(AergoRPCServiceBlockingStub.class);
    when(aergoService.getTX(any())).thenReturn(mock(Blockchain.Tx.class));

    final TransactionTemplate transactionTemplate = new TransactionTemplate(aergoService,
        converter);

    final Hash hash = new Hash(TXHASH);
    final Optional<Transaction> transaction = transactionTemplate.getTransaction(hash);
    assertTrue(transaction.isPresent());
  }

  @Test
  public void testSign() {
    final AergoRPCServiceBlockingStub aergoService = mock(AergoRPCServiceBlockingStub.class);
    final TxBody txBody = TxBody.newBuilder()
        .setSign(copyFrom(randomUUID().toString().getBytes()))
        .build();
    final Blockchain.Tx resultTx = Blockchain.Tx.newBuilder()
        .setHash(copyFrom(TXHASH))
        .setBody(txBody)
        .build();
    when(aergoService.signTX(any())).thenReturn(resultTx);

    final TransactionTemplate transactionTemplate = new TransactionTemplate(aergoService,
        converter);

    final Transaction transaction = new Transaction();
    final Signature signature = transactionTemplate.sign(transaction);
    assertNotNull(signature);
  }

  @Test
  public void testVerify() {
    final AergoRPCServiceBlockingStub aergoService = mock(AergoRPCServiceBlockingStub.class);
    final VerifyResult blockchainVerifyResult = mock(VerifyResult.class);
    when(aergoService.verifyTX(any())).thenReturn(blockchainVerifyResult);
    when(blockchainVerifyResult.getErrorValue()).thenReturn(0);

    final TransactionTemplate transactionTemplate = new TransactionTemplate(aergoService,
        converter);

    final Transaction transaction = new Transaction();
    final boolean verifyResult = transactionTemplate.verify(transaction);
    assertTrue(verifyResult);
  }

  @Test
  public void testCommit() {
    final AergoRPCServiceBlockingStub aergoService = mock(AergoRPCServiceBlockingStub.class);
    CommitResult commitResult = CommitResult.newBuilder()
        .setHash(copyFrom(TXHASH))
        .build();
    final Rpc.CommitResultList commitResultList = Rpc.CommitResultList.newBuilder()
        .addResults(commitResult)
        .build();
    when(aergoService.commitTX(any())).thenReturn(commitResultList);

    final TransactionTemplate transactionTemplate = new TransactionTemplate(aergoService,
        converter);

    final Transaction transaction = new Transaction();
    final Optional<Hash> txHash = transactionTemplate.commit(transaction);
    assertTrue(txHash.isPresent());
  }

}

