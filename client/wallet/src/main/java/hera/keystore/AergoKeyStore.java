/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Arrays.asList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.api.model.KeyFormat;
import hera.exception.HerajException;
import hera.exception.InvalidAuthenticationException;
import hera.key.AergoKey;
import hera.key.KeyCipherStrategy;
import hera.key.KeyFormatV1Strategy;
import hera.key.Signer;
import hera.model.KeyAlias;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@ApiAudience.Public
@ApiStability.Unstable
public class AergoKeyStore extends AbstractKeyStore implements KeyStore {

  protected static final String STORAGE_DIR;
  protected static final String FIELD_VERSION;
  protected static final String KEYSTORE_POSTFIX;
  protected static final String KEYSTORE_SPLITER;
  protected static final String KEYSTORE_TEMPLATE;
  protected static final Pattern STORE_REGEX;

  static {
    STORAGE_DIR = "keystore";
    FIELD_VERSION = "ks_version";
    KEYSTORE_POSTFIX = "keystore.txt";
    KEYSTORE_SPLITER = "__";
    KEYSTORE_TEMPLATE = "%s" + KEYSTORE_SPLITER + KEYSTORE_POSTFIX; // address__${postfix}
    STORE_REGEX = Pattern.compile("[a-zA-Z0-9]+__keystore\\.txt$");
  }

  protected final Object lock = new Object();
  protected final ObjectMapper mapper = new ObjectMapper();
  protected final FilenameFilter filenameFilter = new AergoKeyStoreFilenameFilter();

  protected final File root;
  protected final String encryptVersion;
  protected final Map<String, KeyCipherStrategy<KeyFormat>> version2Format;

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
   * @param root             a keystore root directory
   * @param keyFormatVersion a keyformat version
   */
  public AergoKeyStore(final String root, final String keyFormatVersion) {
    try {
      assertNotNull(root, "KeyStore rootpath must not null");
      assertNotNull(keyFormatVersion, "KeyStore keyformat version must not null");
      logger.debug("Create Aergo KeyStore to {} with version: {}", root, keyFormatVersion);

      final File file = new File(root + "/" + STORAGE_DIR);
      if (file.exists() && file.isFile()) {
        throw new HerajException("Keystore target is a file");
      }
      if (!file.exists()) {
        final boolean mkdirSuccess = file.mkdirs();
        if (!mkdirSuccess) {
          throw new HerajException("Unable to make directory: " + root);
        }
        logger.debug("Create directory: {}", root);
      }
      this.root = file;
      this.encryptVersion = keyFormatVersion;

      final Map<String, KeyCipherStrategy<KeyFormat>> version2Format = new HashMap<>();
      version2Format.put("1", new KeyFormatV1Strategy());
      this.version2Format = version2Format;
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public void save(final Authentication authentication, final AergoKey key) {
    try {
      assertNotNull(authentication, "Authentication must not null");
      assertNotNull(key, "Key must not null");
      logger.debug("Save with authentication: {}, key: {}", authentication, key);

      synchronized (lock) {
        final String identity = authentication.getIdentity().getValue();
        if (hasIdentity(identity)) {
          throw new InvalidAuthenticationException();
        }

        final String path = this.root.getAbsolutePath() + "/" + deriveFilename(identity);
        logger.debug("Save key file path: {}", path);
        try (final OutputStream os = new BufferedOutputStream(new FileOutputStream(path))) {
          final String password = authentication.getPassword();
          final KeyCipherStrategy<KeyFormat> strategy = this.version2Format
              .get(this.encryptVersion);
          final KeyFormat keyFormat = strategy.encrypt(key, password);
          os.write(keyFormat.getBytesValue().getValue());
        }
      }
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Signer load(final Authentication authentication) {
    try {
      assertNotNull(authentication, "Authentication must not null");
      logger.debug("Load with authentication: {}", authentication);

      synchronized (lock) {
        return loadAergoKey(authentication);
      }
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public void remove(final Authentication authentication) {
    try {
      assertNotNull(authentication, "Authentication must not null");
      logger.debug("Remove with authentication: {}", authentication);

      synchronized (lock) {
        final AergoKey loaded = loadAergoKey(authentication);
        // FIXME: refactor not to use loadAergoKey
        if (null != loaded) {
          final File file = loadKeyFile(authentication.getIdentity().getValue());
          final boolean deleted = file.delete();
          if (!deleted) {
            throw new HerajException("Keystore file not deleted for unknown reason");
          }
        }
      }
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public EncryptedPrivateKey export(final Authentication authentication,
      final String password) {
    try {
      assertNotNull(authentication, "Authentication must not null");
      assertNotNull(password, "Password must not null");
      logger.debug("Export with authentication: {}, password: ***", authentication);

      final AergoKey decrypted = loadAergoKey(authentication);
      return decrypted.export(password);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  protected AergoKey loadAergoKey(final Authentication authentication) throws Exception {
    final String identity = authentication.getIdentity().getValue();
    if (!hasIdentity(identity)) {
      throw new InvalidAuthenticationException();
    }

    final File file = loadKeyFile(identity);
    final KeyFormat keyFormat = KeyFormat.of(new BufferedInputStream(new FileInputStream(file)));
    logger.trace("Loaded key file: {}", keyFormat);

    final JsonNode jsonNode = mapper.reader().readTree(keyFormat.getBytesValue().getInputStream());
    final JsonNode jsonVersion = jsonNode.get(FIELD_VERSION);
    if (null == jsonVersion) {
      throw new HerajException("No " + FIELD_VERSION + " field");
    }

    final String version = jsonVersion.asText();
    logger.trace("Version: {}", version);
    final KeyCipherStrategy<KeyFormat> strategy = this.version2Format.get(version);
    return strategy.decrypt(keyFormat, authentication.getPassword());
  }

  @Override
  public List<Identity> listIdentities() {
    try {
      final List<Identity> identities = new ArrayList<>();
      for (final String keyPath : listMatchingFiles()) {
        final String identity = keyPath.split(KEYSTORE_SPLITER)[0];
        identities.add(KeyAlias.of(identity));
      }
      return identities;
    } catch (Exception e) {
      throw converter.convert(e);
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
    final String[] list = this.root.list(filenameFilter);
    if (null == list) {
      throw new HerajException("Unknown error");
    }
    final List<String> matchingList = asList(list);
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

    if (null == files) {
      throw new HerajException("Unknown error");
    }

    if (0 == files.length) {
      throw new HerajException("No such identity " + identity);
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
