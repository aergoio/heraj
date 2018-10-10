/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.junit.Test;

public class LittleEndianDataOutputStreamTest extends AbstractTestCase {

  @Test
  public void testWrite() throws IOException {
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream littleEndianOut = new LittleEndianDataOutputStream(byteOut);
    final byte[] expected = randomUUID().toString().getBytes();
    littleEndianOut.write(expected, 0, expected.length);
    littleEndianOut.close();

    assertTrue(Arrays.equals(expected, byteOut.toByteArray()));
  }

  @Test
  public void testWriteBoolean() throws IOException {
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream littleEndianOut = new LittleEndianDataOutputStream(byteOut);
    littleEndianOut.writeBoolean(true);
    littleEndianOut.writeBoolean(true);
    littleEndianOut.writeBoolean(false);
    littleEndianOut.close();

    assertTrue(Arrays.equals(new byte[] {0x01, 0x01, 0x00}, byteOut.toByteArray()));
  }

  @Test
  public void testWriteByte() throws IOException {
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream littleEndianOut = new LittleEndianDataOutputStream(byteOut);
    littleEndianOut.writeByte(0x01);
    littleEndianOut.writeByte(0x01);
    littleEndianOut.writeByte(0x00);
    littleEndianOut.close();

    assertTrue(Arrays.equals(new byte[] {0x01, 0x01, 0x00}, byteOut.toByteArray()));
  }

  @Test
  public void testWriteShort() throws IOException {
    final short value = (short) 256;
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream littleEndianOut = new LittleEndianDataOutputStream(byteOut);
    littleEndianOut.writeShort(value);
    littleEndianOut.close();

    final byte[] expected =
        ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array();
    assertTrue(Arrays.equals(expected, byteOut.toByteArray()));
  }

  @Test
  public void testWriteChar() throws IOException {
    final short value = 256;
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream littleEndianOut = new LittleEndianDataOutputStream(byteOut);
    littleEndianOut.writeChar(value);
    littleEndianOut.close();

    final byte[] expected =
        ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array();
    assertTrue(Arrays.equals(expected, byteOut.toByteArray()));
  }

  @Test
  public void testWriteInt() throws IOException {
    final int value = randomUUID().hashCode();
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream littleEndianOut = new LittleEndianDataOutputStream(byteOut);
    littleEndianOut.writeInt(value);
    littleEndianOut.close();

    final byte[] expected =
        ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    assertTrue(Arrays.equals(expected, byteOut.toByteArray()));
  }

  @Test
  public void testWriteLong() throws IOException {
    final long value = 256L;
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream littleEndianOut = new LittleEndianDataOutputStream(byteOut);
    littleEndianOut.writeLong(value);
    littleEndianOut.close();

    final byte[] expected =
        ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(value).array();
    assertTrue(Arrays.equals(expected, byteOut.toByteArray()));
  }

  @Test
  public void testWriteFloat() throws IOException {
    final float value = 256f;
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream littleEndianOut = new LittleEndianDataOutputStream(byteOut);
    littleEndianOut.writeFloat(value);
    littleEndianOut.close();

    final byte[] expected =
        ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(value).array();
    assertTrue(Arrays.equals(expected, byteOut.toByteArray()));
  }

  @Test
  public void testWriteDouble() throws IOException {
    final double value = 256d;
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream littleEndianOut = new LittleEndianDataOutputStream(byteOut);
    littleEndianOut.writeDouble(value);
    littleEndianOut.close();

    final byte[] expected =
        ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(value).array();
    assertTrue(Arrays.equals(expected, byteOut.toByteArray()));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testWriteBytes() throws IOException {
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream littleEndianOut = new LittleEndianDataOutputStream(byteOut);
    littleEndianOut.writeBytes(randomUUID().toString());
    littleEndianOut.close();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testWriteChars() throws IOException {
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream littleEndianOut = new LittleEndianDataOutputStream(byteOut);
    littleEndianOut.writeChars(randomUUID().toString());
    littleEndianOut.close();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testWriteUTF() throws IOException {
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream littleEndianOut = new LittleEndianDataOutputStream(byteOut);
    littleEndianOut.writeUTF(randomUUID().toString());
    littleEndianOut.close();
  }

}
