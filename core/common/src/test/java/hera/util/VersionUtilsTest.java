/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import hera.api.model.BytesValue;
import hera.util.VersionUtils;
import org.bouncycastle.util.Arrays;
import org.junit.Test;

public class VersionUtilsTest extends AbstractTestCase {

  @Test
  public void testValidateWithBytesValue() {
    final byte version = randomUUID().toString().getBytes()[0];
    final byte[] source = randomUUID().toString().getBytes();
    final byte[] versionEnveloped = VersionUtils.envelop(source, version);
    VersionUtils.validate(BytesValue.of(versionEnveloped), version);
  }

  @Test
  public void testValidate() {
    final byte version = randomUUID().toString().getBytes()[0];
    final byte[] source = randomUUID().toString().getBytes();
    final byte[] versionEnveloped = VersionUtils.envelop(source, version);
    VersionUtils.validate(versionEnveloped, version);
  }

  @Test
  public void testEnvelopWithBytesValue() {
    final byte version = randomUUID().toString().getBytes()[0];
    final BytesValue source = BytesValue.of(randomUUID().toString().getBytes());
    final BytesValue versionEnveloped = VersionUtils.envelop(source, version);
    assertEquals(version, versionEnveloped.getValue()[0]);
  }

  @Test
  public void testEnvelopWithBytesValueOnEmptyByteArray() {
    final byte version = randomUUID().toString().getBytes()[0];
    final BytesValue source = null;
    final BytesValue versionEnveloped = VersionUtils.envelop(source, version);
    assertEquals(BytesValue.EMPTY, versionEnveloped);
  }

  @Test
  public void testEnvelop() {
    final byte version = randomUUID().toString().getBytes()[0];
    final byte[] source = randomUUID().toString().getBytes();
    final byte[] versionEnveloped = VersionUtils.envelop(source, version);
    assertEquals(version, versionEnveloped[0]);
  }

  @Test
  public void testEnvelopOnEmptyByteArray() {
    final byte version = randomUUID().toString().getBytes()[0];
    final byte[] source = null;
    final byte[] versionEnveloped = VersionUtils.envelop(source, version);
    assertTrue(Arrays.areEqual(new byte[0], versionEnveloped));
  }

  @Test
  public void testTrimWithBytesValue() {
    final byte version = randomUUID().toString().getBytes()[0];
    final BytesValue source = BytesValue.of(randomUUID().toString().getBytes());
    final BytesValue versionEnveloped = VersionUtils.envelop(source, version);
    final BytesValue actual = VersionUtils.trim(versionEnveloped);
    assertEquals(source, actual);
  }

  @Test
  public void testTrimWithBytesValueOnEmptyByteArray() {
    final BytesValue actual = VersionUtils.trim(BytesValue.EMPTY);
    assertEquals(BytesValue.EMPTY, actual);
  }

  @Test
  public void testTrim() {
    final byte[] versionEnveloped = null;
    final byte[] actual = VersionUtils.trim(versionEnveloped);
    assertTrue(Arrays.areEqual(new byte[0], actual));
  }

}
