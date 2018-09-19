/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.util.Base58Utils;
import java.io.IOException;
import java.util.Arrays;

public class ContractTxHash extends Hash {

  /**
   * Create {@code ContractTxHash} with a raw bytes array.
   *
   * @param bytes value
   * @return created {@link ContractTxHash}
   */
  public static ContractTxHash of(final byte[] bytes) {
    if (null == bytes) {
      return new ContractTxHash(null);
    }
    return new ContractTxHash(Arrays.copyOf(bytes, bytes.length));
  }

  /**
   * Create {@code ContractTxHash} with a base58 encoded value.
   *
   * @param encoded base58 encoded value
   * @return created {@link ContractTxHash}
   * @throws IOException when decoding error
   */
  public static ContractTxHash of(final String encoded) throws IOException {
    if (null == encoded) {
      return new ContractTxHash(null);
    }
    return of(Base58Utils.decode(encoded));
  }

  public ContractTxHash(final byte[] value) {
    super(value);
  }

}
