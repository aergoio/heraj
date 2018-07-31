/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import java.io.IOException;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HexOutputStream extends OutputStream {

  protected final OutputStream out;

  @Override
  public void write(int b) throws IOException {
    out.write('0' + ((b >> 4) & 0x0F));
    out.write('0' + ((b) & 0x0F));
  }
}
