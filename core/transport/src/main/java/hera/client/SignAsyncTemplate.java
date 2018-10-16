/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import hera.Context;
import hera.FutureChainer;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.SignAsyncOperation;
import hera.api.SignEitherOperation;
import hera.api.SignOperation;
import hera.api.model.BytesValue;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.RpcException;
import hera.exception.SignException;
import hera.exception.TransactionVerificationException;
import hera.key.AergoKey;
import hera.transport.ModelConverter;
import hera.transport.TransactionConverterFactory;
import io.grpc.ManagedChannel;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Blockchain.Tx;
import types.Rpc;
import types.Rpc.VerifyResult;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
public class SignAsyncTemplate implements SignAsyncOperation, ChannelInjectable {

  protected final Logger logger = getLogger(getClass());

  protected final ModelConverter<Transaction, Blockchain.Tx> transactionConverter =
      new TransactionConverterFactory().create();

  @Setter
  protected Context context;

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  @Override
  public void injectChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
  }

  @Override
  public ResultOrErrorFuture<Signature> sign(final AergoKey key, final Transaction transaction) {
    ResultOrErrorFuture<Signature> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    final Tx rpcTransaction = transactionConverter.convertToRpcModel(transaction);
    final ListenableFuture<Tx> listenableFuture = aergoService.signTX(rpcTransaction);
    FutureChainer<Tx, Signature> callback = new FutureChainer<>(nextFuture, tx -> {
      final BytesValue sign = ofNullable(tx.getBody().getSign()).map(ByteString::toByteArray)
          .filter(bytes -> 0 != bytes.length).map(BytesValue::of)
          .orElseThrow(() -> new RpcException(
              new SignException("Signing failed: sign field is not found at sign result")));
      final TxHash hash =
          ofNullable(tx.getHash()).map(ByteString::toByteArray).filter(bytes -> 0 != bytes.length)
              .map(BytesValue::new).map(TxHash::new).orElseThrow(() -> new RpcException(
                  new SignException("Signing failed: txHash field is not found at sign result")));
      return Signature.of(sign, hash);
    });
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<Boolean> verify(final AergoKey key, final Transaction transaction) {
    ResultOrErrorFuture<Boolean> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    final Tx tx = transactionConverter.convertToRpcModel(transaction);
    ListenableFuture<VerifyResult> listenableFuture = aergoService.verifyTX(tx);
    FutureChainer<VerifyResult, Boolean> callback = new FutureChainer<VerifyResult, Boolean>(
        nextFuture, verifyResult -> Rpc.VerifyStatus.VERIFY_STATUS_OK == verifyResult.getError()) {
      @Override
      public void onSuccess(VerifyResult t) {
        if (Rpc.VerifyStatus.VERIFY_STATUS_OK == t.getError()) {
          super.onSuccess(t);
        } else {
          super.onFailure(new TransactionVerificationException(t.getError()));
        }
      }
    };
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public <T> Optional<T> adapt(final Class<T> adaptor) {
    if (adaptor.isAssignableFrom(SignAsyncOperation.class)) {
      return (Optional<T>) Optional.of(this);
    } else if (adaptor.isAssignableFrom(SignOperation.class)) {
      final SignTemplate signOperation = new SignTemplate();
      signOperation.setContext(context);
      signOperation.injectChannel((ManagedChannel) aergoService.getChannel());
      return (Optional<T>) Optional.of(signOperation);
    } else if (adaptor.isAssignableFrom(SignEitherOperation.class)) {
      final SignEitherTemplate signEitherOperation = new SignEitherTemplate();
      signEitherOperation.setContext(context);
      signEitherOperation.injectChannel((ManagedChannel) aergoService.getChannel());
      return (Optional<T>) Optional.of(signEitherOperation);
    }
    return Optional.empty();
  }

}
