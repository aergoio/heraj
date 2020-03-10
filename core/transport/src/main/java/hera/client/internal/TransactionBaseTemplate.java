/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.internal;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.ChannelInjectable;
import hera.exception.InternalCommitException;
import hera.transport.ModelConverter;
import hera.transport.TransactionConverterFactory;
import hera.transport.TransactionInBlockConverterFactory;
import io.grpc.ManagedChannel;
import java.util.concurrent.Future;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
public class TransactionBaseTemplate implements ChannelInjectable, ContextProviderInjectable {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<Transaction, Blockchain.Tx> transactionConverter =
      new TransactionConverterFactory().create();

  protected final ModelConverter<Transaction, Blockchain.TxInBlock> transactionInBlockConverter =
      new TransactionInBlockConverterFactory().create();

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  protected ContextProvider contextProvider;

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Getter
  private final Function1<TxHash, Future<Transaction>> transactionFunction =
      new Function1<TxHash, Future<Transaction>>() {

        @Override
        public Future<Transaction> apply(final TxHash txHash) {
          logger.debug("Get transaction with txHash: {}", txHash);

          final Rpc.SingleBytes rpcTxHash = Rpc.SingleBytes.newBuilder()
              .setValue(copyFrom(txHash.getBytesValue()))
              .build();
          logger.trace("AergoService getTX arg: {}", rpcTxHash);

          final Future<Blockchain.Tx> rawFuture = aergoService.getTX(rpcTxHash);
          final Future<Transaction> convertedFuture =
              HerajFutures.transform(rawFuture, new Function1<Blockchain.Tx, Transaction>() {

                @Override
                public Transaction apply(final Blockchain.Tx rpcTx) {
                  return transactionConverter.convertToDomainModel(rpcTx);
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function1<TxHash, Future<Transaction>> transactionInBlockFunction =
      new Function1<TxHash, Future<Transaction>>() {

        @Override
        public Future<Transaction> apply(final TxHash txHash) {
          logger.debug("Get transaction with txHash: {}", txHash);

          final Rpc.SingleBytes rpcTxHash = Rpc.SingleBytes.newBuilder()
              .setValue(copyFrom(txHash.getBytesValue()))
              .build();
          logger.trace("AergoService getTX arg: {}", rpcTxHash);

          final Future<Blockchain.TxInBlock> rawFuture =
              aergoService.getBlockTX(rpcTxHash);
          final Future<Transaction> convertedFuture =
              HerajFutures.transform(rawFuture, new Function1<Blockchain.TxInBlock, Transaction>() {

                @Override
                public Transaction apply(final Blockchain.TxInBlock rpcTxInBlock) {
                  return transactionInBlockConverter.convertToDomainModel(rpcTxInBlock);
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function1<Transaction, Future<TxHash>> commitFunction =
      new Function1<Transaction, Future<TxHash>>() {

        @Override
        public Future<TxHash> apply(final Transaction transaction) {
          logger.debug("Commit transaction with signedTx: {}", transaction);

          final Blockchain.Tx rpcTx = transactionConverter.convertToRpcModel(transaction);
          final Blockchain.TxList rpcTxList =
              Blockchain.TxList.newBuilder().addTxs(rpcTx).build();
          logger.trace("AergoService commitTX arg: {}", rpcTxList);

          final Future<Rpc.CommitResultList> rawFuture = aergoService.commitTX(rpcTxList);
          final Future<TxHash> convertedFuture =
              HerajFutures.transform(rawFuture, new Function1<Rpc.CommitResultList, TxHash>() {

                @Override
                public TxHash apply(final Rpc.CommitResultList rpcCommitResultList) {
                  final Rpc.CommitResult rpcCommitResult =
                      rpcCommitResultList.getResultsList().get(0);
                  if (Rpc.CommitStatus.TX_OK != rpcCommitResult.getError()) {
                    throw new InternalCommitException(rpcCommitResult.getError(),
                        rpcCommitResult.getDetail());
                  }
                  return new TxHash(of(rpcCommitResult.getHash().toByteArray()));
                }
              });
          return convertedFuture;
        }
      };

}
