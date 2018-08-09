/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.pki;

import static java.security.Security.addProvider;
import static org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec;
import static org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;
import static org.slf4j.LoggerFactory.getLogger;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;

public class ECDSAKeyGenerator implements KeyGenerator {
  protected static final String ALGORITHM_NAME = "ECDSA";

  protected static final String CURVE_NAME = "secp256k1";

  static {
    addProvider(new BouncyCastleProvider());
  }

  protected final transient Logger logger = getLogger(getClass());

  protected KeyPairGenerator getKeyPairGenerator()
      throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    final ECNamedCurveParameterSpec ecSpec = getParameterSpec(CURVE_NAME);
    final KeyPairGenerator keyPairGenerator =
        KeyPairGenerator.getInstance(ALGORITHM_NAME, PROVIDER_NAME);
    keyPairGenerator.initialize(ecSpec);
    logger.debug("Generator: {}", keyPairGenerator);
    return keyPairGenerator;
  }

  @Override
  public ECDSAKey create() throws Exception {
    final KeyPairGenerator generator = getKeyPairGenerator();
    final KeyPair pair = generator.generateKeyPair();
    final PrivateKey privateKey = pair.getPrivate();
    logger.debug("Private key: {}", privateKey);
    final PublicKey publicKey = pair.getPublic();
    logger.debug("Public key: {}", publicKey);
    return new ECDSAKey(privateKey, publicKey);
  }

  /**
   * Create keypair from encoded private key.
   *
   * @param encodedPrivateKey private key
   *
   * @return key pair to be recovered
   *
   * @throws Exception On failure of recovery
   */
  public ECDSAKey create(final byte[] encodedPrivateKey) throws Exception {
    final ECNamedCurveParameterSpec ecSpec = getParameterSpec(CURVE_NAME);
    ECDomainParameters ecDomainParams = new ECDomainParameters(
        ecSpec.getCurve(),
        ecSpec.getG(),
        ecSpec.getN(),
        ecSpec.getH(),
        ecSpec.getSeed());
    final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encodedPrivateKey);
    final KeyFactory factory = KeyFactory.getInstance(ALGORITHM_NAME);
    final PrivateKey privateKey = factory.generatePrivate(spec);

    final ECPoint Q = ecDomainParams.getG()
        .multiply(((org.bouncycastle.jce.interfaces.ECPrivateKey) privateKey).getD());
    final ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(Q, ecSpec);
    final PublicKey publicKey = factory.generatePublic(ecPublicKeySpec);
    return new ECDSAKey(privateKey, publicKey);
  }
}
