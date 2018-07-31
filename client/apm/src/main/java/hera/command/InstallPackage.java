/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.util.ValidationUtils.assertTrue;
import static java.util.Arrays.asList;

import hera.FileSet;
import java.nio.file.Path;
import java.util.Optional;

public class InstallPackage extends AbstractCommand {

  @Override
  public void execute() throws Exception {
    assertTrue(0 < arguments.size() && arguments.size() < 3);
    final String url = getArgument(0);
    final Optional<String> checkout = getOptionalArgument(1);

    final CloneGit cloneGit = new CloneGit();
    cloneGit.setArguments(asList(url, checkout.orElse("master")));
    cloneGit.execute();

    final Path base = getModuleBasePath();

    final FileSet fileSet = cloneGit.getFileSet();
    fileSet.copyTo(base);
  }
}
