/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.api.tupleorerror.FunctionChain.of;
import static hera.api.tupleorerror.FunctionChain.success;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.BytesValue;
import hera.api.model.KeyHoldable;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function2;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.RpcException;
import hera.exception.TransactionVerificationException;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.AccountStateConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.TransactionConverterFactory;
import io.grpc.ManagedChannel;
import java.util.Optional;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
public class AccountBaseTemplate implements ChannelInjectable {

  protected final Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<AccountState, Blockchain.State> accountStateConverter =
      new AccountStateConverterFactory().create();

  protected final ModelConverter<Transaction, Blockchain.Tx> transactionConverter =
      new TransactionConverterFactory().create();

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
  }

  @Getter
  private final Function1<AccountAddress, ResultOrErrorFuture<AccountState>> stateFunction =
      (address) -> {
        ResultOrErrorFuture<AccountState> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();

        Rpc.SingleBytes bytes = Rpc.SingleBytes.newBuilder()
            .setValue(accountAddressConverter.convertToRpcModel(address)).build();
        ListenableFuture<Blockchain.State> listenableFuture = aergoService.getState(bytes);
        FutureChain<Blockchain.State, AccountState> callback = new FutureChain<>(nextFuture);
        callback.setSuccessHandler(state -> of(() -> {
          final AccountState withoutAddress = accountStateConverter.convertToDomainModel(state);
          return new AccountState(address, withoutAddress.getNonce(), withoutAddress.getBalance());
        }));
        addCallback(listenableFuture, callback, directExecutor());

        return nextFuture;
      };
  @Getter
  private final Function2<Account, Transaction, ResultOrErrorFuture<Signature>> signFunction =
      (account, transaction) -> {
        ResultOrErrorFuture<Signature> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();

        if (account instanceof KeyHoldable) {
          KeyHoldable keyHoldable = (KeyHoldable) account;
          Transaction copy = Transaction.copyOf(transaction);
          BytesValue signature =
              keyHoldable.sign(copy.calculateHash().getBytesValue().get());
          copy.setSignature(Signature.of(signature, null));
          nextFuture.complete(success(Signature.of(signature, copy.calculateHash())));
        } else {
          Blockchain.Tx rpcTransaction =
              transactionConverter.convertToRpcModel(transaction);
          ListenableFuture<Blockchain.Tx> listenableFuture =
              aergoService.signTX(rpcTransaction);
          FutureChain<Blockchain.Tx, Signature> callback = new FutureChain<>(nextFuture);
          callback.setSuccessHandler(tx -> of(() -> {
            BytesValue sign =
                ofNullable(tx.getBody().getSign()).map(ByteString::toByteArray)
                    .filter(bytes -> 0 != bytes.length).map(BytesValue::of)
                    .orElseThrow(() -> new RpcException(
                        "Signing failed: sign field is not found at sign result"));
            TxHash hash = ofNullable(tx.getHash()).map(ByteString::toByteArray)
                .filter(bytes -> 0 != bytes.length).map(BytesValue::new).map(TxHash::new)
                .orElseThrow(() -> new RpcException(
                    "Signing failed: txHash field is not found at sign result"));
            return Signature.of(sign, hash);
          }));
          addCallback(listenableFuture, callback, directExecutor());
        }

        return nextFuture;
      };

  @Getter
  private final Function2<Account, Transaction, ResultOrErrorFuture<Boolean>> verifyFunction =
      (account, transaction) -> {
        ResultOrErrorFuture<Boolean> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();

        if (account instanceof KeyHoldable) {
          KeyHoldable keyHoldable = (KeyHoldable) account;
          Transaction copy = Transaction.copyOf(transaction);
          BytesValue signature = copy.getSignature().getSign();
          copy.setSignature(null);
          nextFuture.complete(
              success(keyHoldable.verify(copy.calculateHash().getBytesValue().get(), signature)));
        } else {
          Blockchain.Tx tx = transactionConverter.convertToRpcModel(transaction);
          ListenableFuture<Rpc.VerifyResult> listenableFuture = aergoService.verifyTX(tx);
          FutureChain<Rpc.VerifyResult, Boolean> callback =
              new FutureChain<Rpc.VerifyResult, Boolean>(nextFuture);
          callback.setSuccessHandler(result -> of(() -> Optional.of(result)
              .map(v -> Rpc.VerifyStatus.VERIFY_STATUS_OK == v.getError())
              .orElseThrow(() -> new TransactionVerificationException(result.getError()))));
          addCallback(listenableFuture, callback, directExecutor());
        }

        return nextFuture;
      };

}
