/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static java.util.Collections.list;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.exception.KeyStoreException;
import hera.key.AergoKey;
import hera.key.Signer;
import hera.model.KeyAlias;
import hera.util.Sha256Utils;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSAKeyGenerator;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public class JavaKeyStore implements KeyStore, JavaKeyStoreInjectable {

  protected final transient Logger logger = getLogger(getClass());

  protected Signer current;

  protected Authentication currentAuth;

  protected java.security.Provider provider = new BouncyCastleProvider();

  @Getter
  @Setter
  @NonNull
  protected volatile java.security.KeyStore javaKeyStore;

  @Override
  public synchronized boolean unlock(final Authentication authentication) {
    try {
      logger.debug("Unlock key with authentication: {}", authentication);
      current = load(authentication);
      currentAuth = digest(authentication);
      return true;
    } catch (Exception e) {
      logger.debug("Unlock failed by {}", e.toString());
      return false;
    }
  }

  @Override
  public synchronized boolean lock(final Authentication authentication) {
    try {
      logger.debug("Lock key with authentication: {}", authentication);
      final Authentication digested = digest(authentication);
      if (!currentAuth.equals(digested)) {
        throw new KeyStoreException("Invalid authentication");
      }

      current = null;
      currentAuth = null;
      return true;
    } catch (Exception e) {
      logger.info("Lock failed by {}", e.toString());
      return false;
    }
  }

  @Override
  public void save(final Authentication authentication, final AergoKey key) {
    try {
      logger.debug("Save key {} with authentication: {}", key, authentication);

      final String alias = authentication.getIdentity().getValue();
      final java.security.PrivateKey privateKey = key.getPrivateKey();
      final char[] rawPassword = authentication.getPassword().toCharArray();
      final Certificate cert = generateCertificate(key);
      final Certificate[] certChain = new Certificate[] {cert};

      javaKeyStore.setKeyEntry(alias, privateKey, rawPassword, certChain);
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  protected Certificate generateCertificate(final AergoKey key)
      throws OperatorCreationException, CertificateException {
    logger.trace("Generate certificate for account: {}", key);
    final Calendar start = Calendar.getInstance();
    final Calendar expiry = Calendar.getInstance();
    expiry.add(Calendar.YEAR, 1);
    final X500Name name = new X500Name("CN=" + key.getAddress().getValue());
    final ContentSigner signer = new JcaContentSignerBuilder("SHA256WithECDSA")
        .setProvider(provider).build(key.getPrivateKey());
    final X509CertificateHolder holder = new X509v3CertificateBuilder(
        name, BigInteger.ONE, start.getTime(), expiry.getTime(), name,
        SubjectPublicKeyInfo.getInstance(key.getPublicKey().getEncoded()))
            .build(signer);
    final Certificate cert = new JcaX509CertificateConverter()
        .setProvider(provider)
        .getCertificate(holder);
    logger.trace("Generated certificate: {}", cert);
    return cert;
  }

  @Override
  public EncryptedPrivateKey export(final Authentication authentication) {
    try {
      logger.debug("Export key with authentication: {}", authentication);
      final AergoKey restored = load(authentication);
      return restored.export(authentication.getPassword());
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public List<Identity> listIdentities() {
    try {
      final List<String> aliases = list(javaKeyStore.aliases());
      logger.trace("Aliases: {}", aliases);

      final List<Identity> storedIdentities = new ArrayList<Identity>();
      for (final String alias : aliases) {
        storedIdentities.add(new KeyAlias(alias));
      }
      return storedIdentities;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public AccountAddress getPrincipal() {
    return null != current ? current.getPrincipal() : null;
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    logger.debug("Sign raw transaction: {}", rawTransaction);
    if (null == current) {
      throw new KeyStoreException("No unlocked account");
    }
    return current.sign(rawTransaction);
  }

  protected AergoKey load(final Authentication authentication)
      throws java.security.KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException,
      Exception {
    final String alias = authentication.getIdentity().getValue();
    final char[] password = authentication.getPassword().toCharArray();
    final java.security.Key privateKey = javaKeyStore.getKey(alias, password);

    return convertPrivateKey(privateKey);
  }

  protected AergoKey convertPrivateKey(final java.security.Key privateKey) throws Exception {
    BigInteger d = null;
    if (privateKey instanceof java.security.interfaces.ECPrivateKey) {
      d = ((java.security.interfaces.ECPrivateKey) privateKey).getS();
    } else if (privateKey instanceof org.bouncycastle.jce.interfaces.ECPrivateKey) {
      d = ((org.bouncycastle.jce.interfaces.ECPrivateKey) privateKey).getD();
    } else {
      throw new UnsupportedOperationException("Unacceptable key type");
    }
    final ECDSAKey ecdsakey = new ECDSAKeyGenerator().create(d);

    return new AergoKey(ecdsakey);
  }

  protected Authentication digest(final Authentication rawAuthentication) {
    final byte[] digestedPassword = Sha256Utils.digest(rawAuthentication.getPassword().getBytes());
    return Authentication.of(rawAuthentication.getIdentity(), new String(digestedPassword));
  }

}
