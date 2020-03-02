/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import hera.api.model.BytesValue;
import hera.api.model.internal.BytesValueUtils;
import org.bouncycastle.util.Arrays;
import org.junit.Test;

public class BytesValueUtilsTest extends AbstractTestCase {

  @Test
  public void testValidateWithBytesValue() {
    final byte prefix = randomUUID().toString().getBytes()[0];
    final byte[] source = randomUUID().toString().getBytes();
    final byte[] appended = BytesValueUtils.append(source, prefix);
    final boolean result = BytesValueUtils.validatePrefix(BytesValue.of(appended), prefix);
    assertTrue(result);
  }

  @Test
  public void testValidate() {
    final byte prefix = randomUUID().toString().getBytes()[0];
    final byte[] source = randomUUID().toString().getBytes();
    final byte[] appended = BytesValueUtils.append(source, prefix);
    final boolean result = BytesValueUtils.validatePrefix(appended, prefix);
    assertTrue(result);
  }

  @Test
  public void testAppendPrefixWithBytesValue() {
    final byte prefix = randomUUID().toString().getBytes()[0];
    final BytesValue source = BytesValue.of(randomUUID().toString().getBytes());
    final BytesValue appended = BytesValueUtils.append(source, prefix);
    assertEquals(prefix, appended.getValue()[0]);
  }

  @Test
  public void testAppendPrefixWithBytesValueOnEmptyByteArray() {
    final byte prefix = randomUUID().toString().getBytes()[0];
    final BytesValue source = null;
    final BytesValue appended = BytesValueUtils.append(source, prefix);
    assertEquals(prefix, appended.getValue()[0]);
  }

  @Test
  public void testAppendPrefix() {
    final byte prefix = randomUUID().toString().getBytes()[0];
    final byte[] source = randomUUID().toString().getBytes();
    final byte[] appended = BytesValueUtils.append(source, prefix);
    assertEquals(prefix, appended[0]);
  }

  @Test
  public void testAppendPrefixOnEmptyByteArray() {
    final byte prefix = randomUUID().toString().getBytes()[0];
    final byte[] source = null;
    final byte[] appended = BytesValueUtils.append(source, prefix);
    assertEquals(prefix, appended[0]);
  }

  @Test
  public void testTrimPrefixWithBytesValue() {
    final byte prefix = randomUUID().toString().getBytes()[0];
    final BytesValue source = BytesValue.of(randomUUID().toString().getBytes());
    final BytesValue prefixAppendPrefixed = BytesValueUtils.append(source, prefix);
    final BytesValue actual = BytesValueUtils.trimPrefix(prefixAppendPrefixed);
    assertEquals(source, actual);
  }

  @Test
  public void testTrimPrefixWithBytesValueOnEmptyByteArray() {
    final BytesValue target = BytesValue.EMPTY;
    final BytesValue actual = BytesValueUtils.trimPrefix(BytesValue.EMPTY);
    assertEquals(target, actual);
  }

  @Test
  public void testTrimPrefix() {
    final byte[] target = null;
    final byte[] actual = BytesValueUtils.trimPrefix(target);
    assertTrue(Arrays.areEqual(new byte[0], actual));
  }

}
