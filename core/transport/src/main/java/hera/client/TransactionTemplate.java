/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.api.TransactionAsyncOperation;
import hera.api.TransactionOperation;
import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.HerajException;
import io.grpc.ManagedChannel;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@RequiredArgsConstructor
public class TransactionTemplate implements TransactionOperation {

  protected final TransactionAsyncOperation transactionAsyncOperation;

  public TransactionTemplate(final ManagedChannel channel) {
    this(newFutureStub(channel));
  }

  public TransactionTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(new TransactionAsyncTemplate(aergoService));
  }

  @Override
  public ResultOrError<Optional<Transaction>> getTransaction(Hash hash) {
    try {
      return transactionAsyncOperation.getTransaction(hash).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ResultOrError<Signature> sign(Transaction transaction) {
    try {
      return transactionAsyncOperation.sign(transaction).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ResultOrError<Boolean> verify(Transaction transaction) {
    try {
      return transactionAsyncOperation.verify(transaction).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ResultOrError<Optional<Hash>> commit(Transaction transaction) {
    try {
      return transactionAsyncOperation.commit(transaction).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }
}
