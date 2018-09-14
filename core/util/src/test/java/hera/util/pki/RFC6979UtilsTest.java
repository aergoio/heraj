/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.pki;

import static org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec;
import static org.junit.Assert.assertEquals;
import hera.AbstractTestCase;
import hera.util.HexUtils;
import java.math.BigInteger;
import javax.crypto.Mac;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.junit.Test;

public class RFC6979UtilsTest extends AbstractTestCase {

  protected static final String CURVE_NAME = "secp256k1";

  protected static final String MAC_ALGORITHM = "HmacSHA256";

  protected static final ECNamedCurveParameterSpec ecSpec = getParameterSpec(CURVE_NAME);

  protected static final ECDomainParameters ecParams = new ECDomainParameters(ecSpec.getCurve(),
      ecSpec.getG(), ecSpec.getN(), ecSpec.getH(), ecSpec.getSeed());

  @Test
  public void testGeneratek() throws Exception {
    final BigInteger d = new BigInteger(
        "76860113961741997882252494935617441283670103007926986413066244023125861537337");
    final BigInteger n = ecParams.getN();
    final Mac mac = Mac.getInstance(MAC_ALGORITHM);
    byte[] hash =
        HexUtils.decode("8456CADF26AC3F738C59EFC15B32C9B92BA004D0C28A8D46DC496BFA4B29C083");

    final BigInteger expectedk = new BigInteger(
        "115662657340616361253217030848815643517129196728765972723191450123812001087705");
    final BigInteger actualk = RFC6979Utils.generatek(d, n, mac, hash);
    assertEquals(expectedk, actualk);
  }

}
