/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static hera.util.TransportUtils.parseToAer;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.model.AccountAddress;
import hera.api.model.BlockHash;
import hera.api.model.Fee;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;

public class TransactionInBlockConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function<Transaction.TxType, Blockchain.TxType> txTypeDomainConverter =
      domainTxType -> {
        switch (domainTxType) {
          case NORMAL:
            return Blockchain.TxType.NORMAL;
          case GOVERNANCE:
            return Blockchain.TxType.GOVERNANCE;
          default:
            return Blockchain.TxType.UNRECOGNIZED;
        }
      };


  protected final Function<Blockchain.TxType, Transaction.TxType> txTypeRpcConverter =
      rpcTxType -> {
        switch (rpcTxType) {
          case NORMAL:
            return Transaction.TxType.NORMAL;
          case GOVERNANCE:
            return Transaction.TxType.GOVERNANCE;
          default:
            return Transaction.TxType.UNRECOGNIZED;
        }
      };

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final Function<Transaction, Blockchain.TxInBlock> domainConverter =
      domainTransaction -> {
        logger.trace("Domain transaction in block: {}", domainTransaction);

        final Blockchain.TxBody.Builder txBodyBuilder = Blockchain.TxBody.newBuilder()
            .setAccount(accountAddressConverter.convertToRpcModel(domainTransaction.getSender()))
            .setRecipient(
                accountAddressConverter.convertToRpcModel(domainTransaction.getRecipient()))
            .setAmount(copyFrom(domainTransaction.getAmount()))
            .setNonce(domainTransaction.getNonce())
            .setPayload(copyFrom(domainTransaction.getPayload()))
            .setType(txTypeDomainConverter.apply(domainTransaction.getTxType()))
            .setSign(copyFrom(domainTransaction.getSignature().getSign()));
        if (null != domainTransaction.getFee()) {
          txBodyBuilder.setPrice(copyFrom(domainTransaction.getFee().getPrice()));
          txBodyBuilder.setLimit(domainTransaction.getFee().getLimit());
        }

        final Blockchain.TxBody txBody = txBodyBuilder.build();

        final Blockchain.Tx rpcTx = Blockchain.Tx.newBuilder()
            .setBody(txBody)
            .setHash(copyFrom(domainTransaction.getHash().getBytesValue()))
            .build();

        final Blockchain.TxIdx rpcTxIdx = Blockchain.TxIdx.newBuilder()
            .setBlockHash(copyFrom(domainTransaction.getBlockHash().getBytesValue()))
            .setIdx(domainTransaction.getIndexInBlock())
            .build();

        return Blockchain.TxInBlock.newBuilder().setTx(rpcTx).setTxIdx(rpcTxIdx).build();
      };

  protected final Function<Blockchain.TxInBlock, Transaction> rpcConverter = rpcTransaction -> {
    logger.trace("Rpc transaction in block: {}", rpcTransaction);
    final Blockchain.TxIdx rpcTxIdx = rpcTransaction.getTxIdx();
    final Blockchain.Tx rpcTx = rpcTransaction.getTx();
    final Blockchain.TxBody txBody = rpcTx.getBody();

    return new Transaction(accountAddressConverter.convertToDomainModel(txBody.getAccount()),
        accountAddressConverter.convertToDomainModel(txBody.getRecipient()),
        parseToAer(txBody.getAmount()),
        txBody.getNonce(),
        Fee.of(parseToAer(txBody.getPrice()), txBody.getLimit()),
        of(txBody.getPayload().toByteArray()),
        txTypeRpcConverter.apply(txBody.getType()),
        Signature.of(of(txBody.getSign().toByteArray())),
        TxHash.of(of(rpcTx.getHash().toByteArray())),
        BlockHash.of(of(rpcTxIdx.getBlockHash().toByteArray())),
        rpcTxIdx.getIdx(),
        !rpcTxIdx.getBlockHash().equals(com.google.protobuf.ByteString.EMPTY));
  };

  public ModelConverter<Transaction, Blockchain.TxInBlock> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
