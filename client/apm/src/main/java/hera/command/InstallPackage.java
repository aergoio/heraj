/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static com.google.common.io.MoreFiles.deleteRecursively;
import static com.google.common.io.MoreFiles.getFileExtension;
import static com.google.common.io.RecursiveDeleteOption.ALLOW_INSECURE;
import static hera.util.FilepathUtils.append;
import static hera.util.ValidationUtils.assertTrue;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.util.Arrays.asList;

import hera.FileSet;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class InstallPackage extends AbstractCommand {

  @Override
  public void execute() throws Exception {
    assertTrue(0 < arguments.size() && arguments.size() < 3);
    final String packageName = getArgument(0);
    logger.debug("URL: {}", packageName);
    final int separatorIndex = packageName.indexOf("/");
    assertTrue(0 < separatorIndex, "Invalid argument: " + packageName);
    final Optional<String> checkout = getOptionalArgument(1);

    final CloneGit cloneGit = new CloneGit();
    cloneGit.setArguments(asList(packageName, checkout.orElse("master")));
    cloneGit.execute();
    final FileSet fileSet = cloneGit.getFileSet();
    logger.debug("FileSet: {}", fileSet.getFileSet());

    final String publishRepository = append(System.getProperty("user.home"), ".aergo_modules");
    final Path publishPath = Paths.get(append(publishRepository, packageName));
    if (exists(publishPath)) {
      deleteRecursively(publishPath, ALLOW_INSECURE);
    }
    createDirectories(publishPath);
    fileSet.copyTo(publishPath);
    printer.println("Successful to install %s.", packageName);
    printer.println("Install path: <green>%s</green>", publishPath);
  }
}
