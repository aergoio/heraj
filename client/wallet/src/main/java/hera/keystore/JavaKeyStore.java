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
import java.lang.reflect.Method;
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
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class JavaKeyStore implements KeyStore {

  protected static final String[] POSSIBLE_GETD_METHODS = {"getS", "getD"};

  protected final Logger logger = getLogger(getClass());

  protected final java.security.Provider bcProvider = new BouncyCastleProvider();

  protected volatile java.security.KeyStore delegate;

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
      this.delegate = java.security.KeyStore.getInstance(type, provider);
      this.delegate.load(inputStream, password);
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public void save(final Authentication authentication, final AergoKey key) {
    try {
      logger.debug("Save key {} with authentication: {}", key, authentication);

      synchronized (this) {
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
    logger.trace("Generate certificate for account: {}", key);
    final Calendar start = Calendar.getInstance();
    final Calendar expiry = Calendar.getInstance();
    expiry.add(Calendar.YEAR, 1);
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
      logger.debug("Load key with authentication: {}", authentication);

      synchronized (this) {
        if (false == isExists(authentication)) {
          throw new InvalidAuthenticationException("Invalid authentication");
        }

        final java.security.Key rawKey = loadRawKey(authentication);
        return convertPrivateKey(rawKey);
      }
    } catch (InvalidAuthenticationException e) {
      throw e;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public void remove(final Authentication authentication) {
    try {
      logger.debug("Export key with authentication: {}", authentication);

      synchronized (this) {
        if (false == isExists(authentication)) {
          throw new InvalidAuthenticationException("Invalid authentication");
        }

        final String alias = authentication.getIdentity().getValue();
        this.delegate.deleteEntry(alias);
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
      logger.debug("Export key with authentication: {}", authentication);

      synchronized (this) {
        if (false == isExists(authentication)) {
          throw new InvalidAuthenticationException("Invalid authentication");
        }

        final java.security.Key rawKey = loadRawKey(authentication);
        final AergoKey recovered = convertPrivateKey(rawKey);
        return recovered.export(password);
      }
    } catch (InvalidAuthenticationException e) {
      throw e;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public List<Identity> listIdentities() {
    try {
      final List<String> aliases = list(delegate.aliases());
      logger.trace("Aliases: {}", aliases);

      final List<Identity> storedIdentities = new ArrayList<>();
      for (final String alias : aliases) {
        storedIdentities.add(new KeyAlias(alias));
      }
      return storedIdentities;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public void store(final String path, final char[] password) {
    try {
      this.delegate.store(new FileOutputStream(path), password);
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
    return rawKey;
  }

  protected AergoKey convertPrivateKey(final java.security.Key privateKey) throws Exception {
    Method getdMethod = null;
    for (int i = 0; i < POSSIBLE_GETD_METHODS.length; ++i) {
      try {
        final String getdMethodName = POSSIBLE_GETD_METHODS[i];
        final Method target = privateKey.getClass().getMethod(getdMethodName);
        if (null != target) {
          getdMethod = target;
          break;
        }
      } catch (NoSuchMethodException e) {
        // continue loop
      }
    }
    if (null == getdMethod) {
      throw new UnsupportedOperationException(
          "Unacceptable key type: " + privateKey.getClass().getName());
    }

    logger.trace("Get d method: {}, class: {}", getdMethod.getName(),
        privateKey.getClass().getName());
    final BigInteger d = (BigInteger) getdMethod.invoke(privateKey);
    final ECDSAKey ecdsakey = new ECDSAKeyGenerator().create(d);

    return new AergoKey(ecdsakey);
  }

}
