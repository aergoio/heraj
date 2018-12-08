/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.api.model.BytesValue.of;
import static hera.util.NumberUtils.postiveToByteArray;
import static hera.util.Sha256Utils.digest;
import static hera.util.VersionUtils.trim;

import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.TxHash;
import hera.exception.HerajException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TransactionUtils {

  /**
   * Calculate a hash of transaction.
   *
   * @param rawTransaction a raw transaction
   * @return a hash of transaction
   */
  public static TxHash calculateHash(final RawTransaction rawTransaction) {
    try {
      final ByteArrayOutputStream raw = new ByteArrayOutputStream();
      final LittleEndianDataOutputStream dataOut = makeStream(raw, rawTransaction);
      dataOut.flush();
      dataOut.close();
      return new TxHash(of(digest(raw.toByteArray())));
    } catch (final IOException e) {
      throw new HerajException(e);
    }
  }

  /**
   * Calculate a hash of transaction.
   *
   * @param rawTransaction a raw transaction
   * @param signature a signature
   * @return a hash of transaction
   */
  public static TxHash calculateHash(final RawTransaction rawTransaction,
      final Signature signature) {
    try {
      final ByteArrayOutputStream raw = new ByteArrayOutputStream();
      final LittleEndianDataOutputStream dataOut = makeStream(raw, rawTransaction);
      dataOut.write(signature.getSign().getValue());
      dataOut.flush();
      dataOut.close();
      return new TxHash(of(digest(raw.toByteArray())));
    } catch (final IOException e) {
      throw new HerajException(e);
    }
  }

  protected static LittleEndianDataOutputStream makeStream(final ByteArrayOutputStream raw,
      final RawTransaction rawTransaction) throws IOException {
    final LittleEndianDataOutputStream dataOut = new LittleEndianDataOutputStream(raw);
    // WARNING : follow the stream order with server
    dataOut.writeLong(rawTransaction.getNonce());
    dataOut.write(trim(rawTransaction.getSender().getBytesValue().getValue()));
    dataOut.write(trim(rawTransaction.getRecipient().getBytesValue().getValue()));
    dataOut.write(postiveToByteArray(rawTransaction.getAmount()));
    dataOut.write(rawTransaction.getPayload().getValue());
    dataOut.writeLong(rawTransaction.getFee().getLimit());
    dataOut.write(postiveToByteArray(rawTransaction.getFee().getPrice()));
    dataOut.writeInt(rawTransaction.getTxType().getIntValue());
    return dataOut;
  }

}
