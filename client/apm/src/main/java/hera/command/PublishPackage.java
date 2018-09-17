/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static com.google.common.io.MoreFiles.deleteRecursively;
import static com.google.common.io.RecursiveDeleteOption.ALLOW_INSECURE;
import static hera.util.FilepathUtils.append;
import static hera.util.ValidationUtils.assertNotNull;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;

import hera.FileSet;
import hera.ProjectFile;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PublishPackage extends AbstractCommand {

  @Override
  public void execute() throws Exception {
    logger.trace("Starting {}...", this);

    final ProjectFile rootProject = readProject();
    final String buildTarget = rootProject.getTarget();
    assertNotNull(buildTarget, "No target!! add target field to aergo.json.");

    if (!exists(Paths.get(buildTarget))) {
      new BuildProject().execute();
    }

    final String publishRepository = append(System.getProperty("user.home"), ".aergo_modules");
    final Path publishPath = Paths.get(append(publishRepository, rootProject.getName()));
    if (exists(publishPath)) {
      deleteRecursively(publishPath, ALLOW_INSECURE);
    }
    createDirectories(publishPath);
    FileSet.from(Paths.get(".")).copyTo(publishPath);
    printer.println("Successful to publish %s.", rootProject.getName());
    printer.println("Publish path: <green>%s</green>", publishPath);
  }
}
