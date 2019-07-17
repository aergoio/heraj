/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.resolver;

import static hera.api.model.BytesValue.of;
import static hera.util.NumberUtils.positiveToByteArray;
import static hera.util.Sha256Utils.digest;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.TxHash;
import hera.exception.HerajException;
import hera.util.LittleEndianDataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public class TransactionHashResolver {

  protected static final Logger logger = getLogger(TransactionHashResolver.class);

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
    dataOut.write(rawTransaction.getSender().getBytesValue().getValue());
    dataOut.write(rawTransaction.getRecipient().getBytesValue().getValue());
    dataOut.write(positiveToByteArray(rawTransaction.getAmount().getValue()));
    dataOut.write(rawTransaction.getPayload().getValue());
    dataOut.writeLong(rawTransaction.getFee().getLimit());
    dataOut.write(positiveToByteArray(rawTransaction.getFee().getPrice().getValue()));
    dataOut.writeInt(rawTransaction.getTxType().getIntValue());
    dataOut.write(rawTransaction.getChainIdHash().getBytesValue().getValue());
    return dataOut;
  }

}
