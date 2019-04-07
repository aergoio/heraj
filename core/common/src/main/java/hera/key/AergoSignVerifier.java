/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static hera.api.model.BytesValue.of;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
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
public class AergoSignVerifier implements TransactionVerifier {

  protected final transient Logger logger = getLogger(getClass());

  protected final TransactionHashResolver transactionHashResolver = new TransactionHashResolver();

  protected final SignatureResolver signatureResolver = new SignatureResolver();

  protected final ECDSAVerifier ecdsaVerifier = new ECDSAVerifier(ECDSAKeyGenerator.ecParams);

  @Override
  public boolean verify(final Transaction transaction) {
    try {
      logger.debug("Verify transaction: {}", transaction);
      final TxHash txHash = transactionHashResolver.calculateHash(transaction);
      final BytesValue plainText = txHash.getBytesValue();
      return verify(transaction.getSender(), plainText, transaction.getSignature());
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  /**
   * Check if {@code Transaction} is valid.
   *
   * @param accountAddress an signer address
   * @param plainText a plain text
   * @param signature a signature to verify
   * @return if valid
   */
  public boolean verify(final AccountAddress accountAddress, final BytesValue plainText,
      final Signature signature) {
    try {
      logger.debug("Verify with address: {}, plainText: {}, signature: {}", accountAddress,
          plainText, signature);
      final ECDSASignature parsedSignature =
          signatureResolver.parse(signature, ecdsaVerifier.getParams().getN());
      return ecdsaVerifier.verify(accountAddress.asPublicKey(), plainText.getInputStream(),
          parsedSignature);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  /**
   * Verify message with base64 encoded signature.
   *
   * @param accountAddress an signer address
   * @param message a message to verify
   * @param base64EncodedSignature a base64 encoded signature
   * @return verification result
   */
  public boolean verifyMessage(final AccountAddress accountAddress, final String message,
      final String base64EncodedSignature) {
    try {
      logger.debug("Verify message with address: {}, message: {}, encodedSignature: {}",
          accountAddress, message, base64EncodedSignature);
      final BytesValue plainText = new BytesValue(message.getBytes());
      final Signature signature = new Signature(of(Base64Utils.decode(base64EncodedSignature)));
      return verify(accountAddress, plainText, signature);
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

}
