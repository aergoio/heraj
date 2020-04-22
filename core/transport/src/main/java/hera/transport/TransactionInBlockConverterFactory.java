/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static hera.util.TransportUtils.parseToAer;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.BlockHash;
import hera.api.model.ChainIdHash;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import org.slf4j.Logger;
import types.Blockchain;
import types.Blockchain.TxInBlock;

@ApiAudience.Private
@ApiStability.Unstable
public class TransactionInBlockConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<Transaction.TxType, Blockchain.TxType> txTypeConverter =
      new TransactionTypeConverterFactory().create();

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
              .setType(txTypeConverter.convertToRpcModel(domainTransaction.getTxType()))
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
              .type(txTypeConverter.convertToDomainModel(txBody.getType()))
              .build();

          final Transaction domainTransaction = Transaction.newBuilder()
              .rawTransaction(rawTransaction)
              .signature(Signature.newBuilder().sign(of(txBody.getSign().toByteArray())).build())
              .hash(new TxHash(of(rpcTx.getHash().toByteArray())))
              .blockHash(new BlockHash(of(rpcTxIdx.getBlockHash().toByteArray())))
              .indexInBlock(rpcTxIdx.getIdx())
              .confirmed(!rpcTxIdx.getBlockHash().equals(com.google.protobuf.ByteString.EMPTY))
              .build();

          logger.trace("Domain transaction in block converted: {}", domainTransaction);
          return domainTransaction;
        }
      };

  public ModelConverter<Transaction, Blockchain.TxInBlock> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
