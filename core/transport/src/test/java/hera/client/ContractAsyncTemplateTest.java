/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.api.model.Hash;
import hera.api.model.Receipt;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.transport.ModelConverter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;

@SuppressWarnings({"rawtypes", "unchecked"})
@PrepareForTest({AergoRPCServiceFutureStub.class, Blockchain.Receipt.class})
public class ContractAsyncTemplateTest extends AbstractTestCase {

  protected static final ModelConverter<Receipt, Blockchain.Receipt> receiptConverter =
      mock(ModelConverter.class);

  @BeforeClass
  public static void setUpBeforeClass() {
    when(receiptConverter.convertToDomainModel(any(Blockchain.Receipt.class)))
        .thenReturn(mock(Receipt.class));
  }

  @Test
  public void testGetReceipt() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.getReceipt(any())).thenReturn(mockListenableFuture);

    final ContractAsyncTemplate accountAsyncTemplate =
        new ContractAsyncTemplate(aergoService, receiptConverter);

    final ResultOrErrorFuture<Receipt> receipt =
        accountAsyncTemplate.getReceipt(Hash.of(randomUUID().toString().getBytes()));
    assertNotNull(receipt);
  }

}
