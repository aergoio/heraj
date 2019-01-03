/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.pki;

import static java.security.Security.addProvider;
import static java.security.Security.getProvider;
import static org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec;
import static org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;
import static org.slf4j.LoggerFactory.getLogger;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;

public class ECDSAKeyGenerator implements KeyGenerator<ECDSAKey> {

  protected static final String KEY_ALGORITHM = "ECDSA";

  protected static final String CURVE_NAME = "secp256k1";

  protected static final ECNamedCurveParameterSpec ecSpec;

  protected static final ECDomainParameters ecParams;

  static {
    java.security.Provider provider = getProvider(PROVIDER_NAME);
    if (provider != null) {
      Security.removeProvider(PROVIDER_NAME);
    }
    addProvider(new BouncyCastleProvider());
    ecSpec = getParameterSpec(CURVE_NAME);
    ecParams = new ECDomainParameters(ecSpec.getCurve(), ecSpec.getG(), ecSpec.getN(),
        ecSpec.getH(), ecSpec.getSeed());
  }

  protected final transient Logger logger = getLogger(getClass());

  protected KeyPairGenerator getKeyPairGenerator()
      throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    final KeyPairGenerator keyPairGenerator =
        KeyPairGenerator.getInstance(KEY_ALGORITHM, PROVIDER_NAME);
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
    return new ECDSAKey(privateKey, publicKey, ecParams);
  }

  /**
   * Create key-pair from encoded private key.
   *
   * @param encodedPrivateKey PKCS #8 encoded private key
   * @return key pair to be recovered
   * @throws Exception On failure of recovery
   */
  public ECDSAKey create(final byte[] encodedPrivateKey) throws Exception {
    final KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
    final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encodedPrivateKey);
    return create(factory.generatePrivate(spec));
  }

  /**
   * Create key-pair from encoded private key.
   *
   * @param d d value of private key
   * @return key pair to be recovered
   * @throws Exception On failure of recovery
   */
  public ECDSAKey create(final BigInteger d) throws Exception {
    final KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
    final ECPrivateKeySpec spec = new ECPrivateKeySpec(d, ecSpec);
    return create(factory.generatePrivate(spec));
  }

  /**
   * Create key-pair from a private key.
   *
   * @param privateKey a private key
   * @return key pair to be recovered
   * @throws Exception On failure of recovery
   */
  public ECDSAKey create(final PrivateKey privateKey) throws Exception {
    final KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
    final ECPoint Q = ecSpec.getG()
        .multiply(((org.bouncycastle.jce.interfaces.ECPrivateKey) privateKey).getD());
    final ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(Q, ecSpec);
    final PublicKey publicKey = factory.generatePublic(ecPublicKeySpec);
    return new ECDSAKey(privateKey, publicKey, ecParams);
  }

}
