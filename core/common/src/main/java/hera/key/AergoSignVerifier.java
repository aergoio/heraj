/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static hera.util.Sha256Utils.digest;
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
import hera.spec.resolver.SignatureResolver;
import hera.spec.resolver.TransactionHashResolver;
import hera.util.Base64Utils;
import hera.util.pki.ECDSAKeyGenerator;
import hera.util.pki.ECDSASignature;
import hera.util.pki.ECDSAVerifier;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
@RequiredArgsConstructor
public class AergoSignVerifier implements TxVerifier {

  protected final transient Logger logger = getLogger(getClass());

  protected final ECDSAVerifier ecdsaVerifier = new ECDSAVerifier(ECDSAKeyGenerator.ecParams);

  @Override
  public boolean verify(final Transaction transaction) {
    try {
      logger.debug("Verify transaction: {}", transaction);
      final TxHash txHash = TransactionHashResolver.calculateHash(transaction.getRawTransaction());
      return verify(transaction.getSender(), txHash, transaction.getSignature());
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  /**
   * Check if {@code signature} is valid for {@code accountAddress} and {@code hash}.
   *
   * @param accountAddress a signer address
   * @param hash a sha256-hashed message
   * @param signature a signature to verify
   *
   * @return if valid
   */
  public boolean verify(final AccountAddress accountAddress, final Hash hash,
      final Signature signature) {
    try {
      logger.debug("Verify with address: {}, hash: {}, signature: {}", accountAddress,
          hash, signature);
      final ECDSASignature parsedSignature =
          SignatureResolver.parse(signature, ecdsaVerifier.getParams().getN());
      return ecdsaVerifier.verify(accountAddress.asPublicKey(), hash.getBytesValue().getValue(),
          parsedSignature);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  /**
   * Check if {@code base64EncodedSignature} is valid for signer {@code accountAddress} and
   * {@code message}. It hashes {@code message} and verify hashed one.
   *
   * @param accountAddress an signer address
   * @param message a message
   * @param base64EncodedSignature a base64 encoded signature
   *
   * @return if valid
   */
  public boolean verifyMessage(final AccountAddress accountAddress, final String message,
      final String base64EncodedSignature) {
    try {
      final BytesValue rawSignature = BytesValue.of(Base64Utils.decode(base64EncodedSignature));
      final Signature signature = Signature.newBuilder().sign(rawSignature).build();
      return verifyMessage(accountAddress, new BytesValue(message.getBytes()), signature);
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  /**
   * Check if {@code signature} is valid for signer {@code accountAddress} and {@code message}. It
   * hashes {@code message} and verify hashed one.
   *
   * @param accountAddress a signer address
   * @param message a message
   * @param signature a signature to verify
   *
   * @return if valid
   */
  public boolean verifyMessage(final AccountAddress accountAddress, final BytesValue message,
      final Signature signature) {
    try {
      logger.debug("Verify with address: {}, message: {}, signature: {}", accountAddress,
          message, signature);
      final ECDSASignature parsedSignature =
          SignatureResolver.parse(signature, ecdsaVerifier.getParams().getN());
      final byte[] hashed = digest(message.getValue());
      return ecdsaVerifier.verify(accountAddress.asPublicKey(), hashed, parsedSignature);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

}
