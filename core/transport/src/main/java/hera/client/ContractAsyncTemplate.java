/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import hera.FutureChainer;
import hera.api.ContractAsyncOperation;
import hera.api.model.Hash;
import hera.api.model.Receipt;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.transport.ModelConverter;
import hera.transport.ReceiptConverterFactory;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@RequiredArgsConstructor
public class ContractAsyncTemplate implements ContractAsyncOperation {
  protected final Logger logger = getLogger(getClass());

  protected final AergoRPCServiceFutureStub aergoService;

  protected final ModelConverter<Receipt, Blockchain.Receipt> contractConverter;

  public ContractAsyncTemplate(final ManagedChannel channel) {
    this(newFutureStub(channel));
  }

  public ContractAsyncTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(aergoService, new ReceiptConverterFactory().create());
  }

  @Override
  public ResultOrErrorFuture<Receipt> getReceipt(final Hash hash) {
    ResultOrErrorFuture<Receipt> nextFuture = new ResultOrErrorFuture<>();

    final ByteString byteString = copyFrom(hash);
    final Rpc.SingleBytes hashBytes = Rpc.SingleBytes.newBuilder().setValue(byteString).build();
    final ListenableFuture<Blockchain.Receipt> listenableFuture =
        aergoService.getReceipt(hashBytes);
    FutureChainer<Blockchain.Receipt, Receipt> callback =
        new FutureChainer<>(nextFuture, r -> contractConverter.convertToDomainModel(r));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

}
