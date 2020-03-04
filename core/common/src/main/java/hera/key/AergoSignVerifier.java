/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static hera.key.AccountAddressSpec.recoverPublicKey;
import static hera.key.SignatureSpec.deserialize;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.exception.HerajException;
import hera.util.Sha256Utils;
import hera.util.pki.ECDSAKeyGenerator;
import hera.util.pki.ECDSASignature;
import hera.util.pki.ECDSAVerifier;
import java.security.PublicKey;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class AergoSignVerifier implements Verifier {

  // minimum length of a DER encoded signature which both R and S are 1 byte each.
  // <header-magic> + <1-byte> + <int-marker> + 0x01 + <r.byte> + <int-marker> + 0x01 + <s.byte>
  public static final int SIGN_MINIMUM_LENGTH = 8;

  protected final transient Logger logger = getLogger(getClass());

  protected final ECDSAVerifier ecdsaVerifier = new ECDSAVerifier(ECDSAKeyGenerator.ecParams);

  @Override
  public boolean verify(final Transaction transaction) {
    try {
      logger.debug("Verify transaction: {}", transaction);
      final TxHash txHash = transaction.getRawTransaction().calculateHash();
      return verify(transaction.getSender(), txHash, transaction.getSignature());
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public boolean verify(final AccountAddress accountAddress, final BytesValue message,
      final Signature signature) {
    try {
      final byte[] hashed = Sha256Utils.digest(message.getValue());
      return verify(accountAddress, Hash.of(BytesValue.of(hashed)), signature);
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public boolean verify(final AccountAddress accountAddress, final Hash hashedMessage,
      final Signature signature) {
    try {
      logger.debug("Verify with address: {}, hashed message: {}, signature: {}", accountAddress,
          hashedMessage, signature);
      final PublicKey publicKey = recoverPublicKey(accountAddress);
      final ECDSASignature ecdsaSignature = deserialize(signature);
      return ecdsaVerifier.verify(publicKey, hashedMessage.getBytesValue().getValue(),
          ecdsaSignature);
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

}
