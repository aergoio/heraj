/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static hera.util.TransportUtils.parseToAer;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.BlockHash;
import hera.api.model.ChainIdHash;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.Transaction.TxType;
import hera.api.model.TxHash;
import org.slf4j.Logger;
import types.Blockchain;
import types.Blockchain.TxInBlock;

public class TransactionInBlockConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<Transaction.TxType, Blockchain.TxType> txTypeDomainConverter =
      new Function1<Transaction.TxType, Blockchain.TxType>() {

        @Override
        public Blockchain.TxType apply(final Transaction.TxType domainTxType) {
          switch (domainTxType) {
            case NORMAL:
              return Blockchain.TxType.NORMAL;
            case GOVERNANCE:
              return Blockchain.TxType.GOVERNANCE;
            case REDEPLOY:
              return Blockchain.TxType.REDEPLOY;
            default:
              return Blockchain.TxType.UNRECOGNIZED;
          }
        }
      };

  protected final Function1<Blockchain.TxType, Transaction.TxType> txTypeRpcConverter =
      new Function1<Blockchain.TxType, Transaction.TxType>() {

        @Override
        public TxType apply(Blockchain.TxType rpcTxType) {
          switch (rpcTxType) {
            case NORMAL:
              return Transaction.TxType.NORMAL;
            case GOVERNANCE:
              return Transaction.TxType.GOVERNANCE;
            case REDEPLOY:
              return Transaction.TxType.REDEPLOY;
            default:
              return Transaction.TxType.UNRECOGNIZED;
          }
        }
      };

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final Function1<Transaction, Blockchain.TxInBlock> domainConverter =
      new Function1<Transaction, Blockchain.TxInBlock>() {

        @Override
        public Blockchain.TxInBlock apply(final Transaction domainTransaction) {
          logger.trace("Domain transaction in block to convert: {}", domainTransaction);

          final Blockchain.TxBody.Builder txBodyBuilder = Blockchain.TxBody.newBuilder()
              .setChainIdHash(copyFrom(domainTransaction.getChainIdHash().getBytesValue()))
              .setAccount(accountAddressConverter.convertToRpcModel(domainTransaction.getSender()))
              .setRecipient(
                  accountAddressConverter.convertToRpcModel(domainTransaction.getRecipient()))
              .setAmount(copyFrom(domainTransaction.getAmount()))
              .setNonce(domainTransaction.getNonce())
              .setPayload(copyFrom(domainTransaction.getPayload()))
              .setType(txTypeDomainConverter.apply(domainTransaction.getTxType()))
              .setSign(copyFrom(domainTransaction.getSignature().getSign()))
              .setGasPrice(copyFrom(domainTransaction.getFee().getPrice()))
              .setGasLimit(domainTransaction.getFee().getLimit());

          final Blockchain.TxBody txBody = txBodyBuilder.build();

          final Blockchain.Tx rpcTx = Blockchain.Tx.newBuilder()
              .setBody(txBody)
              .setHash(copyFrom(domainTransaction.getHash().getBytesValue()))
              .build();

          final Blockchain.TxIdx rpcTxIdx = Blockchain.TxIdx.newBuilder()
              .setBlockHash(copyFrom(domainTransaction.getBlockHash().getBytesValue()))
              .setIdx(domainTransaction.getIndexInBlock())
              .build();

          final TxInBlock rpcInBlock =
              Blockchain.TxInBlock.newBuilder().setTx(rpcTx).setTxIdx(rpcTxIdx).build();
          logger.trace("Rpc transaction in block converted: {}", rpcInBlock);
          return rpcInBlock;
        }
      };

  protected final Function1<Blockchain.TxInBlock, Transaction> rpcConverter =
      new Function1<Blockchain.TxInBlock, Transaction>() {

        @Override
        public Transaction apply(final Blockchain.TxInBlock rpcTransaction) {
          logger.trace("Rpc transaction in block to convert: {}", rpcTransaction);
          final Blockchain.TxIdx rpcTxIdx = rpcTransaction.getTxIdx();
          final Blockchain.Tx rpcTx = rpcTransaction.getTx();
          final Blockchain.TxBody txBody = rpcTx.getBody();

          final RawTransaction rawTransaction = RawTransaction.newBuilder()
              .chainIdHash(new ChainIdHash(of(txBody.getChainIdHash().toByteArray())))
              .from(accountAddressConverter.convertToDomainModel(txBody.getAccount()))
              .to(accountAddressConverter.convertToDomainModel(txBody.getRecipient()))
              .amount(parseToAer(txBody.getAmount()))
              .nonce(txBody.getNonce())
              .fee(new Fee(parseToAer(txBody.getGasPrice()), txBody.getGasLimit()))
              .payload(of(txBody.getPayload().toByteArray()))
              .type(txTypeRpcConverter.apply(txBody.getType()))
              .build();

          final Transaction domainTransaction = new Transaction(
              rawTransaction,
              new Signature(of(txBody.getSign().toByteArray())),
              new TxHash(of(rpcTx.getHash().toByteArray())),
              new BlockHash(of(rpcTxIdx.getBlockHash().toByteArray())),
              rpcTxIdx.getIdx(),
              !rpcTxIdx.getBlockHash().equals(com.google.protobuf.ByteString.EMPTY));

          logger.trace("Domain transaction in block converted: {}", domainTransaction);
          return domainTransaction;
        }
      };

  public ModelConverter<Transaction, Blockchain.TxInBlock> create() {
    return new ModelConverter<Transaction, Blockchain.TxInBlock>(domainConverter, rpcConverter);
  }

}
