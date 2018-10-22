/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An implementation of {@link DataOutput} that uses little-endian byte ordering for writing
 * {@code char}, {@code short}, {@code int}, {@code float}, {@code double}, and {@code long} values.
 * <br>
 * We don't support {@link #writeBytes(String)}, {@link #writeChars(String)},
 * {@link #writeUTF(String)}.
 *
 * @author taeiklim
 *
 */
public final class LittleEndianDataOutputStream extends FilterOutputStream implements DataOutput {

  public LittleEndianDataOutputStream(OutputStream out) {
    super(new DataOutputStream(out));
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    out.write(b, off, len);
  }

  @Override
  public void writeBoolean(boolean v) throws IOException {
    ((DataOutputStream) out).writeBoolean(v);
  }

  @Override
  public void writeByte(int v) throws IOException {
    ((DataOutputStream) out).writeByte(v);
  }

  /**
   * Write the short value in a little endian order. The byte values to be written, in the order
   * shown, are:
   *
   * <pre>
   * {@code
   * (byte)(0xff & v)
   * (byte)(0xff & (v >> 8))
   * }
   * </pre>
   */
  @Override
  public void writeShort(int v) throws IOException {
    out.write(0xFF & v);
    out.write(0xFF & (v >> 8));
  }

  /**
   * Write the char value in a little endian order. The byte values to be written, in the order
   * shown, are:
   *
   * <pre>
   * {@code
   * (byte)(0xff & v)
   * (byte)(0xff & (v >> 8))
   * }
   * </pre>
   */
  @Override
  public void writeChar(int v) throws IOException {
    writeShort(v);
  }

  /**
   * Write the int value in a little endian order. The byte values to be written, in the order
   * shown, are:
   *
   * <pre>
   * {@code
   * (byte)(0xff & v)
   * (byte)(0xff & (v >> 8))
   * (byte)(0xff & (v >> 16))
   * (byte)(0xff & (v >> 24))
   * }
   * </pre>
   */
  @Override
  public void writeInt(int v) throws IOException {
    out.write(0xFF & v);
    out.write(0xFF & (v >> 8));
    out.write(0xFF & (v >> 16));
    out.write(0xFF & (v >> 24));
  }

  /**
   * Write the long value in a little endian order. The byte values to be written, in the order
   * shown, are:
   *
   * <pre>
   * {@code
   * (byte)(0xff & v)
   * (byte)(0xff & (v >> 8))
   * (byte)(0xff & (v >> 16))
   * (byte)(0xff & (v >> 24))
   * (byte)(0xff & (v >> 32))
   * (byte)(0xff & (v >> 40))
   * (byte)(0xff & (v >> 48))
   * (byte)(0xff & (v >> 56))
   * }
   * </pre>
   */
  @Override
  public void writeLong(long v) throws IOException {
    out.write((int) (0xFF & v));
    out.write((int) (0xFF & (v >> 8)));
    out.write((int) (0xFF & (v >> 16)));
    out.write((int) (0xFF & (v >> 24)));
    out.write((int) (0xFF & (v >> 32)));
    out.write((int) (0xFF & (v >> 40)));
    out.write((int) (0xFF & (v >> 48)));
    out.write((int) (0xFF & (v >> 56)));
  }

  /**
   * Write the float value in a little endian order.
   */
  @Override
  public void writeFloat(float v) throws IOException {
    writeInt(Float.floatToIntBits(v));
  }

  /**
   * Write the double value in a little endian order.
   */
  @Override
  public void writeDouble(double v) throws IOException {
    writeLong(Double.doubleToLongBits(v));
  }

  /**
   * UnSupported operation.
   */
  @Override
  public void writeBytes(String s) throws IOException {
    throw new UnsupportedOperationException();
  }

  /**
   * UnSupported operation.
   */
  @Override
  public void writeChars(String s) throws IOException {
    throw new UnsupportedOperationException();
  }

  /**
   * UnSupported operation.
   */
  @Override
  public void writeUTF(String str) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() throws IOException {
    out.close();
  }

}
