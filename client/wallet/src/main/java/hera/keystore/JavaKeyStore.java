/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.list;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.exception.InvalidAuthenticationException;
import hera.exception.KeyStoreException;
import hera.key.AergoKey;
import hera.key.Signer;
import hera.model.KeyAlias;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSAKeyGenerator;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class JavaKeyStore implements KeyStore {

  protected final transient Logger logger = getLogger(getClass());

  protected final Object lock = new Object();
  protected final java.security.Provider bcProvider = new BouncyCastleProvider();
  protected final java.security.KeyStore delegate;

  /**
   * Create a keystore which uses {@link java.security.KeyStore}.
   *
   * @param delegate a java keystore
   *
   * @throws KeyStoreException on keystore error
   */
  public JavaKeyStore(final java.security.KeyStore delegate) {
    try {
      assertNotNull(delegate);
      logger.debug("Create JKS with delegate: {}", delegate);
      this.delegate = delegate;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  /**
   * Create a keystore which uses {@link java.security.KeyStore}.
   *
   * @param type a keystore type see {@link java.security.KeyStore#getInstance(String)}
   *
   * @throws KeyStoreException on keystore error
   */
  public JavaKeyStore(final String type) {
    this(type, null, null);
  }

  /**
   * Create a keystore which uses {@link java.security.KeyStore}.
   *
   * @param type a keystore type see
   *        {@link java.security.KeyStore#getInstance(String, java.security.Provider)}
   * @param provider a keystore provider
   *
   * @throws KeyStoreException on keystore error
   */
  public JavaKeyStore(final String type, final java.security.Provider provider) {
    this(type, provider, null, null);
  }

  /**
   * Create a keystore which uses {@link java.security.KeyStore}.
   *
   * @param type a keystore type see {@link java.security.KeyStore#getInstance(String)}
   * @param inputStream an input stream for keystore
   * @param password a keystore password
   *
   * @throws KeyStoreException on keystore error
   */
  public JavaKeyStore(final String type, final InputStream inputStream, final char[] password) {
    try {
      assertNotNull(type, "Keystore type must not null");
      logger.debug("Create JKS with type: {}", type);
      this.delegate = java.security.KeyStore.getInstance(type);
      this.delegate.load(inputStream, password);
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  /**
   * Create a keystore which uses {@link java.security.KeyStore}.
   *
   * @param type a keystore type see
   *        {@link java.security.KeyStore#getInstance(String, java.security.Provider)}
   * @param provider a keystore provider
   * @param inputStream an input stream for keystore
   * @param password a keystore password
   *
   * @throws KeyStoreException on keystore error
   */
  public JavaKeyStore(final String type, final java.security.Provider provider,
      final InputStream inputStream, final char[] password) {
    try {
      assertNotNull(type, "Keystore type must not null");
      assertNotNull(provider, "Keystore provider must not null");
      logger.debug("Create JKS with type: {}, provider: {}", type, provider);
      this.delegate = java.security.KeyStore.getInstance(type, provider);
      this.delegate.load(inputStream, password);
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public void save(final Authentication authentication, final AergoKey key) {
    try {
      assertNotNull(authentication, "Save authentication must not null");
      assertNotNull(key, "Save key must not null");
      logger.debug("Save with authentication: {}, key: {}", KeyStoreConstants.CREDENTIALS,
          key.getAddress());

      synchronized (lock) {
        if (isExists(authentication)) {
          throw new InvalidAuthenticationException("Invalid authentication");
        }
        final String alias = authentication.getIdentity().getValue();
        final java.security.PrivateKey privateKey = key.getPrivateKey();
        final char[] rawPassword = authentication.getPassword().toCharArray();
        final Certificate cert = generateCertificate(key);
        final Certificate[] certChain = new Certificate[] {cert};
        this.delegate.setKeyEntry(alias, privateKey, rawPassword, certChain);
      }
    } catch (InvalidAuthenticationException e) {
      throw e;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  protected Certificate generateCertificate(final AergoKey key)
      throws OperatorCreationException, CertificateException {
    logger.trace("Generate certificate for account: {}", key.getAddress());
    final Calendar start = Calendar.getInstance();
    final Calendar expiry = Calendar.getInstance();
    expiry.add(Calendar.YEAR, 1);
    logger.trace("Start: {}, expiry: {}", start, expiry);
    final X500Name name = new X500Name("CN=" + key.getAddress().getValue());
    final ContentSigner signer = new JcaContentSignerBuilder("SHA256WithECDSA")
        .setProvider(bcProvider).build(key.getPrivateKey());
    final X509CertificateHolder holder = new X509v3CertificateBuilder(
        name, BigInteger.ONE, start.getTime(), expiry.getTime(), name,
        SubjectPublicKeyInfo.getInstance(key.getPublicKey().getEncoded()))
            .build(signer);
    final Certificate cert = new JcaX509CertificateConverter()
        .setProvider(bcProvider)
        .getCertificate(holder);
    logger.trace("Generated certificate: {}", cert);
    return cert;
  }

  @Override
  public Signer load(final Authentication authentication) {
    try {
      assertNotNull(authentication, "Load authentication must not null");
      logger.debug("Load with authentication: {}", KeyStoreConstants.CREDENTIALS);

      java.security.Key rawKey = null;
      synchronized (lock) {
        if (false == isExists(authentication)) {
          throw new InvalidAuthenticationException("Invalid authentication");
        }
        rawKey = loadRawKey(authentication);
      }

      final AergoKey aergoKey = convertPrivateKey(rawKey);
      logger.trace("Loaded key: {}", aergoKey);
      return aergoKey;
    } catch (InvalidAuthenticationException e) {
      throw e;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public void remove(final Authentication authentication) {
    try {
      assertNotNull(authentication, "Remove authentication must not null");
      logger.debug("Remove with authentication: {}", KeyStoreConstants.CREDENTIALS);

      synchronized (lock) {
        if (false == isExists(authentication)) {
          throw new InvalidAuthenticationException("Invalid authentication");
        }
        final Identity identity = authentication.getIdentity();
        this.delegate.deleteEntry(identity.getValue());
      }
    } catch (InvalidAuthenticationException e) {
      throw e;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public EncryptedPrivateKey export(final Authentication authentication, final String password) {
    try {
      assertNotNull(authentication, "Export authentication must not null");
      assertNotNull(password, "Export password must not null");
      logger.debug("Export with authentication: {}, password: {}", KeyStoreConstants.CREDENTIALS,
          KeyStoreConstants.CREDENTIALS);

      java.security.Key rawKey = null;
      synchronized (lock) {
        if (false == isExists(authentication)) {
          throw new InvalidAuthenticationException("Invalid authentication");
        }
        rawKey = loadRawKey(authentication);
      }
      final AergoKey decrypted = convertPrivateKey(rawKey);
      return decrypted.export(password);
    } catch (InvalidAuthenticationException e) {
      throw e;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public List<Identity> listIdentities() {
    try {
      List<String> aliases = null;
      synchronized (lock) {
        aliases = list(this.delegate.aliases());
        logger.trace("Aliases: {}", aliases);
      }

      final List<Identity> identities = new ArrayList<>();
      for (final String alias : aliases) {
        identities.add(new KeyAlias(alias));
      }
      logger.debug("Identities: {}", identities);
      return identities;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public void store(final String path, final char[] password) {
    try (final FileOutputStream os = new FileOutputStream(path)) {
      logger.debug("Save JKS to path: {}", path);
      synchronized (lock) {
        this.delegate.store(os, password);
      }
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  protected boolean isExists(final Authentication authentication)
      throws java.security.KeyStoreException, NoSuchAlgorithmException {
    final String alias = authentication.getIdentity().getValue();
    if (this.delegate.containsAlias(alias)) {
      return true;
    }

    try {
      final java.security.Key rawKey = loadRawKey(authentication);
      if (null != rawKey) {
        return true;
      }
      return false;
    } catch (UnrecoverableKeyException e) {
      // decrypt failure
      return false;
    } catch (java.security.KeyStoreException | NoSuchAlgorithmException e) {
      throw e;
    }
  }

  protected java.security.Key loadRawKey(final Authentication authentication)
      throws UnrecoverableKeyException, java.security.KeyStoreException, NoSuchAlgorithmException {
    final String alias = authentication.getIdentity().getValue();
    final char[] rawPassword = authentication.getPassword().toCharArray();
    final java.security.Key rawKey = delegate.getKey(alias, rawPassword);
    Arrays.fill(rawPassword, '0');
    return rawKey;
  }

  protected AergoKey convertPrivateKey(final java.security.Key privateKey) throws Exception {
    BigInteger d = null;
    if (privateKey instanceof java.security.interfaces.ECPrivateKey) {
      d = ((java.security.interfaces.ECPrivateKey) privateKey).getS();
    } else if (privateKey instanceof org.bouncycastle.jce.interfaces.ECPrivateKey) {
      d = ((org.bouncycastle.jce.interfaces.ECPrivateKey) privateKey).getD();
    } else {
      throw new UnsupportedOperationException(
          "Unacceptable key type: " + privateKey.getClass().getName());
    }
    final ECDSAKey ecdsakey = new ECDSAKeyGenerator().create(d);
    return new AergoKey(ecdsakey);
  }

}
