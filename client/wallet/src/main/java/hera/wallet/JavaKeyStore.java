/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static java.util.Collections.list;
import static java.util.Collections.newSetFromMap;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.exception.InvalidAuthentiationException;
import hera.exception.LockedAccountException;
import hera.exception.WalletException;
import hera.key.AergoKey;
import hera.key.Signer;
import hera.util.Sha256Utils;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSAKeyGenerator;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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

  protected Set<Authentication> unlockedAuthSet =
      newSetFromMap(new ConcurrentHashMap<Authentication, Boolean>());

  protected Map<AccountAddress, AergoKey> address2Unlocked =
      new ConcurrentHashMap<AccountAddress, AergoKey>();

  @Override
  public void saveKey(final AergoKey key, final String password) {
    try {
      final Certificate cert = generateCertificate(key);
      delegate.setKeyEntry(key.getAddress().getAlias(), key.getPrivateKey(), password.toCharArray(),
          new Certificate[] {cert});
    } catch (Exception e) {
      throw new WalletException(e);
    }
  }

  protected Certificate generateCertificate(final AergoKey key)
      throws OperatorCreationException, CertificateException {
    final Calendar start = Calendar.getInstance();
    final Calendar expiry = Calendar.getInstance();
    expiry.add(Calendar.YEAR, 1);
    final X500Name name = new X500Name("CN=" + key.getAddress().getAlias());
    final ContentSigner signer = new JcaContentSignerBuilder("SHA256WithECDSA")
        .setProvider(provider).build(key.getPrivateKey());
    final X509CertificateHolder holder = new X509v3CertificateBuilder(
        name, BigInteger.ONE, start.getTime(), expiry.getTime(), name,
        SubjectPublicKeyInfo.getInstance(key.getPublicKey().getEncoded()))
            .build(signer);
    final Certificate cert = new JcaX509CertificateConverter()
        .setProvider(provider)
        .getCertificate(holder);
    return cert;
  }

  @Override
  public EncryptedPrivateKey export(final Authentication authentication) {
    try {
      final Key privateKey = delegate.getKey(authentication.getAddress().getAlias(),
          authentication.getPassword().toCharArray());
      return restoreKey(privateKey).export(authentication.getPassword());
    } catch (KeyStoreException e) {
      throw new WalletException(e);
    } catch (Exception e) {
      throw new InvalidAuthentiationException(e);
    }
  }

  @Override
  public List<AccountAddress> listStoredAddresses() {
    try {
      List<AccountAddress> storedAddresses = new ArrayList<AccountAddress>();
      ArrayList<String> aliases = list(delegate.aliases());
      for (final String alias : aliases) {
        storedAddresses.add(AccountAddress.fromAlias(alias));
      }
      return storedAddresses;
    } catch (KeyStoreException e) {
      throw new WalletException(e);
    } catch (Exception e) {
      throw new InvalidAuthentiationException(e);
    }
  }

  @Override
  public Account unlock(final Authentication authentication) {
    try {
      Key privateKey = delegate.getKey(authentication.getAddress().getAlias(),
          authentication.getPassword().toCharArray());
      final AergoKey key = restoreKey(privateKey);
      unlockedAuthSet.add(digest(authentication));
      address2Unlocked.put(key.getAddress(), key);
      return new AccountFactory().create(key.getAddress(), this);
    } catch (KeyStoreException e) {
      throw new WalletException(e);
    } catch (Exception e) {
      throw new InvalidAuthentiationException(e);
    }
  }

  protected AergoKey restoreKey(final Key privateKey) throws Exception {
    BigInteger d = null;
    if (privateKey instanceof java.security.interfaces.ECPrivateKey) {
      d = ((java.security.interfaces.ECPrivateKey) privateKey).getS();
    } else if (privateKey instanceof org.bouncycastle.jce.interfaces.ECPrivateKey) {
      d = ((org.bouncycastle.jce.interfaces.ECPrivateKey) privateKey).getD();
    } else {
      throw new WalletException("Unacceptable key type");
    }
    final ECDSAKey ecdsakey = new ECDSAKeyGenerator().create(d);
    return new AergoKey(ecdsakey);
  }

  @Override
  public void lock(final Authentication authentication) {
    final Authentication digested = digest(authentication);
    if (false == unlockedAuthSet.contains(digested)) {
      throw new InvalidAuthentiationException("Unable to lock account");
    }
    unlockedAuthSet.remove(digested);
    address2Unlocked.remove(authentication.getAddress());
  }

  protected Authentication digest(final Authentication rawAuthentication) {
    final byte[] digestedPassword = Sha256Utils.digest(rawAuthentication.getPassword().getBytes());
    return Authentication.of(rawAuthentication.getAddress(), new String(digestedPassword));
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    final AccountAddress sender = rawTransaction.getSender();
    if (null == sender) {
      throw new WalletException("Sender is null");
    }
    try {
      return getUnlockedKey(sender).sign(rawTransaction);
    } catch (Exception e) {
      throw new WalletException(e);
    }
  }

  @Override
  public boolean verify(final Transaction transaction) {
    final AccountAddress sender = transaction.getSender();
    if (null == sender) {
      throw new WalletException("Sender is null");
    }

    try {
      return getUnlockedKey(sender).verify(transaction);
    } catch (Exception e) {
      throw new WalletException(e);
    }
  }

  protected AergoKey getUnlockedKey(final AccountAddress address) {
    final AergoKey key = address2Unlocked.get(address);
    if (null == key) {
      throw new LockedAccountException();
    }
    return key;
  }

  @Override
  public void store(final String path, final String password) {
    try {
      delegate.store(new FileOutputStream(new File(path)), password.toCharArray());
    } catch (Exception e) {
      throw new WalletException(e);
    }
  }
}
