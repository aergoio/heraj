/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static hera.util.AddressUtils.deriveAddress;
import static java.util.Collections.list;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
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
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class JavaKeyStore implements KeyStore, Signer {

  protected final Logger logger = getLogger(getClass());

  protected java.security.Provider provider = new BouncyCastleProvider();

  @NonNull
  protected final java.security.KeyStore delegate;

  protected Authentication currentUnlockedAuth;
  protected AergoKey currentUnlockedKey;

  @Override
  public void saveKey(final AergoKey key, final Authentication authentication) {
    try {
      logger.debug("Save key: {}, authentication: {}", authentication);
      final String alias = authentication.getIdentity().getInfo();
      final PrivateKey privateKey = key.getPrivateKey();
      final char[] rawPassword = authentication.getPassword().toCharArray();
      final Certificate cert = generateCertificate(key);
      final Certificate[] certChain = new Certificate[] {cert};
      delegate.setKeyEntry(alias, privateKey, rawPassword, certChain);
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  protected Certificate generateCertificate(final AergoKey key)
      throws OperatorCreationException, CertificateException {
    final Calendar start = Calendar.getInstance();
    final Calendar expiry = Calendar.getInstance();
    expiry.add(Calendar.YEAR, 1);
    final X500Name name = new X500Name("CN=" + key.getAddress().getInfo());
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
      final Key privateKey = delegate.getKey(authentication.getIdentity().getInfo(),
          authentication.getPassword().toCharArray());
      final AergoKey restored = restoreKey(privateKey);
      return restored.export(authentication.getPassword());
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public List<Identity> listIdentities() {
    try {
      final List<Identity> storedIdentities = new ArrayList<Identity>();
      final List<String> aliases = list(delegate.aliases());
      logger.trace("Aliases: {}", aliases);
      for (final String alias : aliases) {
        Identity identity = null;
        try {
          identity = deriveAddress(new Identity() {

            @Override
            public String getInfo() {
              return alias;
            }
          });
        } catch (Exception e) {
          identity = new KeyAlias(alias);
        }
        storedIdentities.add(identity);
      }
      return storedIdentities;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public Account unlock(final Authentication authentication) {
    try {
      logger.debug("Unlock key with authentication: {}", authentication);
      final java.security.Key privateKey = delegate.getKey(authentication.getIdentity().getInfo(),
          authentication.getPassword().toCharArray());
      final AergoKey key = restoreKey(privateKey);
      currentUnlockedAuth = digest(authentication);
      currentUnlockedKey = key;
      return new AccountFactory().create(key.getAddress(), this);
    } catch (Exception e) {
      logger.debug("Unlock failed by {}", e.toString());
      return null;
    }
  }

  protected AergoKey restoreKey(final Key privateKey) throws Exception {
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

  @Override
  public boolean lock(final Authentication authentication) {
    try {
      logger.debug("Lock key with authentication: {}", authentication);
      final Authentication digested = digest(authentication);
      if (!currentUnlockedAuth.equals(digested)) {
        return false;
      }
      currentUnlockedAuth = null;
      currentUnlockedKey = null;
      return true;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  protected Authentication digest(final Authentication rawAuthentication) {
    final byte[] digestedPassword = Sha256Utils.digest(rawAuthentication.getPassword().getBytes());
    return Authentication.of(rawAuthentication.getIdentity(), new String(digestedPassword));
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    try {
      logger.debug("Sign raw transaction: {}", rawTransaction);
      final AccountAddress sender = rawTransaction.getSender();
      if (null == sender) {
        throw new IllegalArgumentException("Sender is null");
      }
      final AergoKey key = getUnlockedKey(sender);
      return key.sign(rawTransaction);
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public boolean verify(final Transaction transaction) {
    try {
      logger.debug("Verify signed transaction: {}", transaction);
      final AccountAddress sender = transaction.getSender();
      if (null == sender) {
        throw new IllegalArgumentException("Sender is null");
      }
      return getUnlockedKey(sender).verify(transaction);
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  protected AergoKey getUnlockedKey(final AccountAddress address) {
    if (null == currentUnlockedKey || !currentUnlockedKey.getAddress().equals(address)) {
      throw new IllegalStateException("Unlock account first");
    }
    return currentUnlockedKey;
  }

  @Override
  public void store(final String path, final String password) {
    try {
      if (logger.isDebugEnabled()) {
        logger.debug("Store keystore with path: {}, password: {}", path,
            Sha256Utils.digest(password.getBytes()));
      }
      delegate.store(new FileOutputStream(new File(path)), password.toCharArray());
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

}
