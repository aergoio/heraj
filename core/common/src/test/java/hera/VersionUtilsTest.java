/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.bouncycastle.util.Arrays;
import org.junit.Test;

public class VersionUtilsTest extends AbstractTestCase {

  @Test
  public void testValidate() {
    final byte version = randomUUID().toString().getBytes()[0];
    final byte[] source = randomUUID().toString().getBytes();
    final byte[] versionEnveloped = VersionUtils.envelop(source, version);
    VersionUtils.validate(versionEnveloped, version);
  }

  @Test
  public void testEnvelop() {
    final byte version = randomUUID().toString().getBytes()[0];
    final byte[] source = randomUUID().toString().getBytes();
    final byte[] versionEnveloped = VersionUtils.envelop(source, version);
    assertEquals(versionEnveloped[0], version);
  }

  @Test
  public void testTrim() {
    final byte version = randomUUID().toString().getBytes()[0];
    final byte[] source = randomUUID().toString().getBytes();
    final byte[] versionEnveloped = VersionUtils.envelop(source, version);
    final byte[] actual = VersionUtils.trim(versionEnveloped);
    assertTrue(Arrays.areEqual(source, actual));
  }

}
