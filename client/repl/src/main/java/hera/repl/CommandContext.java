/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl;

import java.io.Closeable;
import lombok.Getter;
import lombok.Setter;
import org.jline.reader.LineReader;

public class CommandContext implements Closeable {
  @Getter
  protected boolean alive = true;

  @Getter
  @Setter
  protected LineReader lineReader;

  @Override
  public void close() {
    alive = false;
  }
}
