/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static hera.util.IoUtils.from;
import static hera.util.Sha256Utils.digest;
import static org.slf4j.LoggerFactory.getLogger;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Decoder;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.exception.HerajException;
import hera.spec.resolver.AddressResolver;
import hera.spec.resolver.SignatureResolver;
import hera.spec.resolver.TransactionHashResolver;
import hera.util.pki.ECDSAKeyGenerator;
import hera.util.pki.ECDSASignature;
import hera.util.pki.ECDSAVerifier;
import java.io.StringReader;
import java.security.PublicKey;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class AergoSignVerifier implements Verifier {

  protected final transient Logger logger = getLogger(getClass());

  protected final ECDSAVerifier ecdsaVerifier = new ECDSAVerifier(ECDSAKeyGenerator.ecParams);

  @Override
  public boolean verify(final Transaction transaction) {
    try {
      logger.debug("Verify transaction: {}", transaction);
      final TxHash txHash = TransactionHashResolver.calculateHash(transaction.getRawTransaction());
      return verifyMessage(transaction.getSender(), txHash, transaction.getSignature());
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public boolean verifyMessage(final AccountAddress accountAddress, final String message,
      final String base64EncodedSignature) {
    return verifyMessage(accountAddress, message, base64EncodedSignature, Decoder.Base64);
  }

  @Override
  public boolean verifyMessage(final AccountAddress accountAddress, final String message,
      final String encodedSignature, final Decoder decoder) {
    try {
      final BytesValue rawSignature =
          BytesValue.of(from(decoder.decode(new StringReader(encodedSignature))));
      final Signature signature = Signature.newBuilder().sign(rawSignature).build();
      return verifyMessage(accountAddress, BytesValue.of(message.getBytes()), signature);
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public boolean verifyMessage(final AccountAddress accountAddress, final BytesValue message,
      final Signature signature) {
    try {
      final Hash hashedMessage = Hash.of(BytesValue.of(digest(message.getValue())));
      return verifyMessage(accountAddress, hashedMessage, signature);
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public boolean verifyMessage(final AccountAddress accountAddress, final Hash hashedMessage,
      final Signature signature) {
    try {
      logger.debug("Verify with address: {}, hash: {}, signature: {}", accountAddress,
          hashedMessage, signature);
      final ECDSASignature parsedSignature =
          SignatureResolver.parse(signature, ecdsaVerifier.getParams().getN());
      final PublicKey publicKey = AddressResolver.recoverPublicKey(accountAddress);
      return ecdsaVerifier.verify(publicKey, hashedMessage.getBytesValue().getValue(),
          parsedSignature);
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

}
