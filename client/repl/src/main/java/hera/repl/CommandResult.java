/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandResult {
  protected final Supplier<String> printer;

  @Override
  public String toString() {
    return printer.get();
  }
}
