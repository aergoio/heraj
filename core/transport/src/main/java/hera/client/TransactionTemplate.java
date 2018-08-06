/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.util.TransportUtils.copyFrom;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newBlockingStub;

import com.google.protobuf.ByteString;
import hera.api.TransactionOperation;
import hera.api.model.BytesValue;
import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.transport.ModelConverter;
import hera.transport.TransactionConverterFactory;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.Blockchain.Tx;
import types.Blockchain.TxList;
import types.Rpc.CommitStatus;
import types.Rpc.SingleBytes;
import types.Rpc.VerifyResult;

@RequiredArgsConstructor
public class TransactionTemplate implements TransactionOperation {

  protected final Logger logger = getLogger(getClass());

  protected final AergoRPCServiceBlockingStub aergoService;

  protected final ModelConverter<Transaction, Tx> transactionConverter;

  public TransactionTemplate(final ManagedChannel channel) {
    this(newBlockingStub(channel));
  }

  public TransactionTemplate(final AergoRPCServiceBlockingStub aergoService) {
    this(aergoService, new TransactionConverterFactory().create());
  }

  @Override
  public Optional<Transaction> getTransaction(final Hash hash) {
    try {
      final ByteString byteString = copyFrom(hash);
      final SingleBytes bytes = SingleBytes.newBuilder().setValue(byteString).build();
      final Tx tx = aergoService.getTX(bytes);
      return ofNullable(transactionConverter.convertToDomainModel(tx));
    } catch (final StatusRuntimeException e) {
      logger.trace("Unexpected exception: {}", e.getStatus());
      if (ofNullable(e.getStatus()).map(Status::getCode)
          .filter(code -> Status.NOT_FOUND.getCode() == code).isPresent()) {
        return empty();
      }
      throw e;
    }
  }

  @Override
  public Signature sign(final Transaction transaction) {
    final Tx tx = transactionConverter.convertToRpcModel(transaction);
    final Tx resultTx = aergoService.signTX(tx);
    final BytesValue sign = ofNullable(resultTx.getBody().getSign())
        .map(ByteString::toByteArray)
        .filter(bytes -> 0 != bytes.length)
        .map(BytesValue::of)
        .orElseThrow(IllegalArgumentException::new);
    final Hash hash = ofNullable(resultTx.getHash())
        .map(ByteString::toByteArray)
        .filter(bytes -> 0 != bytes.length)
        .map(Hash::new)
        .orElseThrow(IllegalArgumentException::new);
    return Signature.of(sign, hash);
  }

  @Override
  public boolean verify(final Transaction transaction) {
    final Tx tx = transactionConverter.convertToRpcModel(transaction);
    try {
      final VerifyResult verifyResult = aergoService.verifyTX(tx);
      return 0 == verifyResult.getErrorValue();
    } catch (StatusRuntimeException e) {
      return false;
    }
  }

  @Override
  public Optional<Hash> commit(final Transaction transaction) {
    try {
      final Tx tx = transactionConverter.convertToRpcModel(transaction);
      final TxList txList = TxList.newBuilder().addTxs(tx).build();
      return aergoService.commitTX(txList).getResultsList().stream()
          .filter(r -> r.getError() == CommitStatus.COMMIT_STATUS_OK)
          .map(r -> r.getHash().toByteArray())
          .map(Hash::new)
          .findFirst();
    } catch (StatusRuntimeException e) {
      logger.trace("Unexpected exception: {}", e.getStatus());
      if (ofNullable(e.getStatus()).map(Status::getCode)
          .filter(code -> Status.NOT_FOUND.getCode() == code).isPresent()) {
        return empty();
      }
      throw e;
    }
  }
}
