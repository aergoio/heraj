/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.util.HexUtils.dump;
import static hera.util.IoUtils.from;
import static java.util.Arrays.asList;

import java.io.IOException;
import org.junit.Test;

public class CloneGitIT extends AbstractIT {
  @Test
  public void testExecute() throws Exception {
    final CloneGit cloneGit = new CloneGit();
    cloneGit.setArguments(asList("bylee/arc-sample", "master"));
    cloneGit.execute();

    cloneGit.stream().parallel().forEach(file -> {
      try {
        logger.trace("File: {}\n{}", file, dump(from(file.open())));
      } catch (final IOException e) {
        throw new IllegalStateException();
      }
    });
  }
}