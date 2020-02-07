/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.exception.InvalidAuthenticationException;
import hera.exception.InvalidKeyStoreFormatException;
import hera.exception.KeyStoreException;
import hera.key.AergoKey;
import hera.key.Signer;
import hera.model.KeyAlias;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.slf4j.Logger;

public class AergoKeyStore implements KeyStore {

  protected static final String STORAGE_DIR = "keystore";
  protected static final String FIELD_VERSION = "ks_version";
  protected static final String KEYSTORE_POSTFIX = "keystore.txt";
  protected static final String KEYSTORE_SPLITER = "__";
  protected static final String KEYSTORE_TEMPLATE = "%s" + KEYSTORE_SPLITER
      + KEYSTORE_POSTFIX; // address__${postfix}
  protected static final Pattern STORE_REGEX = Pattern.compile("[a-zA-Z0-9]+__keystore\\.txt$");

  protected static final String KEYSTORE_DELIM_PATTERN = "[\\r\\n\\s]+";

  protected final transient Logger logger = getLogger(getClass());

  protected final Object lock = new Object();
  protected final ObjectMapper mapper = new ObjectMapper();
  protected final FilenameFilter filenameFilter = new AergoKeyStoreFilenameFilter();

  protected final File root;
  protected final String encryptVersion;
  protected final HashMap<String, KeyFormatStrategy> version2Format;

  /**
   * Create aergo keystore with root directory {@code keyStoreDir}.
   *
   * @param root a keystore root directory
   */
  public AergoKeyStore(final String root) {
    this(root, "1");
  }

  /**
   * Create aergo keystore with root directory {@code keyStoreDir}.
   *
   * @param root a keystore root directory
   * @param keyFormatVersion a keyformat version
   */
  public AergoKeyStore(final String root, final String keyFormatVersion) {
    assertNotNull(root, "KeyStore rootpath must not null");
    assertNotNull(keyFormatVersion, "KeyStore keyformat version must not null");
    logger.debug("Create Aergo KeyStore to {} with version: {}", root, keyFormatVersion);

    final File file = new File(root + "/" + STORAGE_DIR);
    if (file.exists() && file.isFile()) {
      throw new KeyStoreException("Keystore target is a file");
    }
    if (!file.exists()) {
      final boolean mkdirSuccess = file.mkdirs();
      if (!mkdirSuccess) {
        throw new KeyStoreException("Unable to make directory: " + root);
      }
      logger.debug("Create directory: {}", root);
    }
    this.root = file;
    this.encryptVersion = keyFormatVersion;

    final HashMap<String, KeyFormatStrategy> version2Format = new HashMap<>();
    final KeyFormatStrategyFactory factory = new KeyFormatStrategyFactory();
    final List<String> versions = factory.listSupportedVersion();
    logger.debug("Supported key format versions: {}", versions);
    for (final String version : versions) {
      version2Format.put(version, factory.create(version));
    }
    this.version2Format = version2Format;
  }

  @Override
  public void save(final Authentication authentication, final AergoKey key) {
    try {
      assertNotNull(authentication, "Save authentication must not null");
      assertNotNull(key, "Save target key must not null");
      logger.debug("Save with authentication: {}, key: {}", KeyStoreConstants.CREDENTIALS,
          key.getAddress());

      synchronized (lock) {
        final String identity = authentication.getIdentity().getValue();
        if (hasIdentity(identity)) {
          throw new InvalidAuthenticationException();
        }

        final String path = this.root.getAbsolutePath() + "/" + deriveFilename(identity);
        logger.debug("Save key file path: {}", path);
        try (final OutputStream os = new BufferedOutputStream(new FileOutputStream(path))) {
          final char[] password = authentication.getPassword().toCharArray();
          final KeyFormatStrategy strategy = this.version2Format.get(this.encryptVersion);
          final String json = strategy.encrypt(key, password);
          Arrays.fill(password, '0');
          os.write(json.getBytes());
        }
      }
    } catch (KeyStoreException e) {
      throw e;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public Signer load(final Authentication authentication) {
    try {
      assertNotNull(authentication, "Load authentication must not null");
      logger.debug("Load with authentication: {}", KeyStoreConstants.CREDENTIALS);

      synchronized (lock) {
        return loadAergoKey(authentication);
      }
    } catch (KeyStoreException e) {
      throw e;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public void remove(final Authentication authentication) {
    try {
      assertNotNull(authentication, "Remove authentication must not null");
      logger.debug("Remove with authentication: {}", authentication);

      synchronized (lock) {
        final AergoKey loaded = loadAergoKey(authentication);
        // FIXME: refactor not to use loadAergoKey
        if (null != loaded) {
          final File file = loadKeyFile(authentication.getIdentity().getValue());
          final boolean deleted = file.delete();
          if (!deleted) {
            throw new IllegalStateException("Keystore file not deleted for unknown reason");
          }
        }
      }
    } catch (KeyStoreException e) {
      throw e;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public EncryptedPrivateKey export(final Authentication authentication,
      final String password) {
    try {
      assertNotNull(authentication, "Export authentication must not null");
      assertNotNull(password, "Export password must not null");
      logger.debug("Export with authentication: {}, password: {}", KeyStoreConstants.CREDENTIALS,
          KeyStoreConstants.CREDENTIALS);

      final AergoKey decrypted = loadAergoKey(authentication);
      logger.trace("Address to export: {}", decrypted.getAddress());
      return decrypted.export(password);
    } catch (KeyStoreException e) {
      throw e;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  protected AergoKey loadAergoKey(final Authentication authentication)
      throws JsonProcessingException, IOException {
    final String identity = authentication.getIdentity().getValue();
    if (!hasIdentity(identity)) {
      throw new InvalidAuthenticationException();
    }

    final File file = loadKeyFile(identity);
    try (
        final Scanner scanner = new Scanner(new BufferedInputStream(new FileInputStream(file)))) {
      scanner.useDelimiter(KEYSTORE_DELIM_PATTERN);
      final StringBuilder sb = new StringBuilder();
      while (scanner.hasNext()) {
        sb.append(scanner.next());
      }
      final String json = sb.toString();
      logger.trace("Loaded key file: {}", json);

      final JsonNode jsonNode = mapper.reader().readTree(json);
      final JsonNode jsonVersion = jsonNode.get(FIELD_VERSION);
      if (null == jsonVersion) {
        throw new InvalidKeyStoreFormatException(
            "No " + FIELD_VERSION + " field");
      }

      final String version = jsonVersion.asText();
      logger.trace("Version: {}", version);
      final KeyFormatStrategy strategy = this.version2Format.get(version);
      final char[] decryptPassword = authentication.getPassword().toCharArray();
      final AergoKey decrypted = strategy.decrypt(json, decryptPassword);
      Arrays.fill(decryptPassword, '0');

      return decrypted;
    }
  }

  @Override
  public List<Identity> listIdentities() {
    try {
      final List<Identity> identities = new ArrayList<>();
      for (final String keyPath : listMatchingFiles()) {
        final String identity = keyPath.split(KEYSTORE_SPLITER)[0];
        identities.add(new KeyAlias(identity));
      }
      logger.debug("Identities: {}", identities);
      return identities;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public void store(String path, char[] password) {
    // do nothing
  }

  protected boolean hasIdentity(final String identity) {
    return listMatchingFiles().contains(deriveFilename(identity));
  }

  protected List<String> listMatchingFiles() {
    logger.trace("Matcher: {}", STORE_REGEX);
    final List<String> matchingList = Arrays.asList(this.root.list(filenameFilter));
    logger.trace("Matching files: {}", matchingList);
    return matchingList;
  }

  protected File loadKeyFile(final String identity) {
    final String filename = deriveFilename(identity);
    final File[] files = this.root.listFiles(new FilenameFilter() {

      @Override
      public boolean accept(final File dir, final String name) {
        return name.equals(filename);
      }
    });
    if (0 == files.length) {
      throw new IllegalArgumentException("No such identity " + identity);
    }
    return files[0];
  }

  protected String deriveFilename(final String identity) {
    return String.format(KEYSTORE_TEMPLATE, identity);
  }

  private class AergoKeyStoreFilenameFilter implements FilenameFilter {

    @Override
    public boolean accept(final File dir, final String name) {
      logger.trace("  Evaluate {}/{}", dir, name);
      return STORE_REGEX.matcher(name).matches();
    }

  }

}
