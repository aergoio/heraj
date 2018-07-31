/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.command;

import hera.repl.Command;
import java.util.List;
import lombok.Setter;

public abstract class AbstractCommand implements Command {
  @Setter
  protected List<String> arguments;
}
