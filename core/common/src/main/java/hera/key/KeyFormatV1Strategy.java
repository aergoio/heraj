/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.KeyFormat;
import hera.exception.HerajException;
import hera.util.HexUtils;
import hera.util.Sha256Utils;
import hera.util.pki.ECDSAKeyGenerator;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public class KeyFormatV1Strategy implements KeyCipherStrategy<KeyFormat> {

  protected static final String CHARSET = "UTF-8";

  // version
  protected static final String VERSION = "1";

  // cipher
  protected static final String CIPHER_ALGORITHM = "aes-128-ctr";
  protected static final String KDF_ALGORITHM = "scrypt";

  // kdf
  protected static final int SCRIPT_N_STANDARD = 1 << 18; // cpu, memory cost
  protected static final int SCRIPT_P_STANDARD = 1; // parallelism
  protected static final int SCRIPT_R = 8; // block size
  protected static final int SCRIPT_DKLEN = 32; // devived key length

  protected static final String ALGORITHM = "AES/CTR/NoPadding";
  protected static final String KEYSPEC = "AES";

  protected final Logger logger = getLogger(getClass());

  protected final SecureRandom random = new SecureRandom();

  protected final ObjectMapper mapper = new ObjectMapper();

  @Override
  public KeyFormat encrypt(final AergoKey key, final String password) {
    try {
      logger.debug("Encrypt with key: {}, password: [CREDENTIALS]", key.getAddress());
      final KdfParams kdfParams = getNewKdfParams();

      // encrypt raw key
      final byte[] derivedPassword = deriveCipherKey(password.getBytes(CHARSET), kdfParams);
      final byte[] encryptKey = Arrays.copyOf(derivedPassword, 16);
      final byte[] rawPrivateKey = key.getRawPrivateKey().getValue();
      final byte[] iv = randomBytes(16);
      final byte[] rawCiphertext = encryptWithKeySpec(iv, encryptKey, rawPrivateKey);
      final byte[] rawMac = generateMac(derivedPassword, rawCiphertext);

      // cipher
      final String ciphertext = HexUtils.encodeLower(rawCiphertext);
      final Cipher cipher = new Cipher();
      cipher.setAlgorithm(CIPHER_ALGORITHM);
      cipher.setCiphertext(ciphertext);
      final CipherParams cipherParams = new CipherParams();
      cipherParams.setIv(HexUtils.encodeLower(iv));
      cipher.setParams(cipherParams);

      // mac
      final String mac = HexUtils.encodeLower(rawMac);
      final Kdf kdf = new Kdf();
      kdf.setAlgorithm(KDF_ALGORITHM);
      kdf.setMac(mac);
      kdf.setParams(kdfParams);

      // keystore
      final String address = key.getAddress().getEncoded();
      final String version = VERSION;
      final V1KeyStore v1KeyStore = new V1KeyStore(address, version, cipher, kdf);

      final byte[] raw = mapper.writeValueAsBytes(v1KeyStore);
      return KeyFormat.of(BytesValue.of(raw));
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  protected KdfParams getNewKdfParams() {
    final KdfParams params = new KdfParams();
    params.setN(SCRIPT_N_STANDARD);
    params.setP(SCRIPT_P_STANDARD);
    params.setR(SCRIPT_R);
    params.setDklen(SCRIPT_DKLEN);
    final byte[] rawSalt = randomBytes(32);
    params.setSalt(HexUtils.encodeLower(rawSalt));
    return params;
  }

  protected byte[] randomBytes(final int length) {
    final byte[] bytes = new byte[length];
    random.nextBytes(bytes);
    return bytes;
  }

  protected byte[] encryptWithKeySpec(final byte[] iv, final byte[] encryptKey,
      final byte[] plaintext) throws Exception {
    final IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
    final javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(ALGORITHM);
    final SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey, KEYSPEC);
    cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
    return cipher.doFinal(plaintext);
  }

  @Override
  public AergoKey decrypt(final KeyFormat keyFormat, final String password) {
    try {
      final JsonNode jsonNode = mapper.reader()
          .readTree(keyFormat.getBytesValue().getInputStream());
      final V1KeyStore v1KeyStore = parse(jsonNode);

      if (!VERSION.equals(v1KeyStore.getVersion())) {
        throw new HerajException("Keystore version must be " + VERSION);
      }

      final Cipher cipher = v1KeyStore.getCipher();
      if (!CIPHER_ALGORITHM.equals(cipher.getAlgorithm())) {
        throw new HerajException("cipher algorithm must be " + CIPHER_ALGORITHM);
      }

      final Kdf kdf = v1KeyStore.getKdf();
      if (!KDF_ALGORITHM.equals(kdf.getAlgorithm())) {
        throw new HerajException("kdf algorithm must be " + KDF_ALGORITHM);
      }

      // password, ciphertext
      final byte[] derivedPassword = deriveCipherKey(password.getBytes(CHARSET), kdf.getParams());
      final byte[] ciphertext = HexUtils.decode(cipher.getCiphertext());

      // mac check
      final byte[] derivedMac = generateMac(derivedPassword, ciphertext);
      final byte[] actualMac = HexUtils.decode(kdf.getMac());
      if (!Arrays.areEqual(actualMac, derivedMac)) {
        throw new HerajException("Invalid mac value");
      }

      // decrypt
      final byte[] iv = HexUtils.decode(cipher.getParams().getIv());
      final byte[] decryptKey = Arrays.copyOf(derivedPassword, 16);
      final byte[] rawD = decryptWithKeySpec(iv, decryptKey, ciphertext);
      final BigInteger d = new BigInteger(1, rawD);
      final AergoKey aergoKey = new AergoKey(new ECDSAKeyGenerator().create(d));

      if (!v1KeyStore.getAddress().equals(aergoKey.getAddress().getEncoded())) {
        throw new HerajException("Invalid address");
      }

      return aergoKey;
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  protected V1KeyStore parse(final JsonNode jsonNode)
      throws JsonParseException, JsonMappingException, IOException {
    return mapper.treeToValue(jsonNode, V1KeyStore.class);
  }

  protected byte[] decryptWithKeySpec(final byte[] iv, final byte[] encryptKey,
      final byte[] ciphertext) throws Exception {
    final IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
    final javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(ALGORITHM);
    final SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey, KEYSPEC);
    cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
    return cipher.doFinal(ciphertext);
  }

  protected byte[] deriveCipherKey(final byte[] password, final KdfParams kdfParams) {
    final byte[] rawSalt = HexUtils.decode(kdfParams.getSalt());
    return SCrypt.generate(password, rawSalt, kdfParams.getN(), kdfParams.getR(),
        kdfParams.getP(), kdfParams.getDklen());
  }

  protected byte[] generateMac(byte[] derivedKey, byte[] cipherText) {
    final byte[] rawMac = new byte[16 + cipherText.length];
    System.arraycopy(derivedKey, 16, rawMac, 0, 16);
    System.arraycopy(cipherText, 0, rawMac, 16, cipherText.length);
    return generateHash(rawMac);
  }

  protected byte[] generateHash(final byte[] message) {
    return Sha256Utils.digest(message);
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  protected static final class V1KeyStore {

    @JsonProperty("aergo_address")
    protected String address;
    @JsonProperty("ks_version")
    protected String version;

    protected Cipher cipher;
    protected Kdf kdf;
  }

  @Data
  @JsonRootName("cipher")
  protected static final class Cipher {

    protected String algorithm;
    protected CipherParams params;
    protected String ciphertext;
  }

  @Data
  @JsonRootName("params")
  protected static final class CipherParams {

    protected String iv;
  }

  @Data
  @JsonRootName("kdf")
  protected static final class Kdf {

    protected String algorithm;
    protected KdfParams params;
    protected String mac;
  }

  @Data
  @JsonRootName("params")
  protected static final class KdfParams {

    protected int dklen;
    protected int n;
    protected int p;
    protected int r;
    protected String salt;
  }

}
