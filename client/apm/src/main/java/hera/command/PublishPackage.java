/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static com.google.common.io.MoreFiles.deleteRecursively;
import static com.google.common.io.RecursiveDeleteOption.ALLOW_INSECURE;
import static hera.util.FilepathUtils.append;
import static hera.util.ValidationUtils.assertNotNull;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.list;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import hera.ProjectFile;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PublishPackage extends AbstractCommand {

  @Override
  public void execute() throws Exception {
    logger.trace("Starting {}...", this);

    final ReadProjectFile readProjectFile = new ReadProjectFile();
    readProjectFile.setArguments(asList(getProjectFile().toString()));
    readProjectFile.execute();

    final ProjectFile rootProject = readProjectFile.getProject();
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
    copyRecursively(Paths.get("."), publishPath);
  }

  protected void copyRecursively(final Path source, final Path destination) throws IOException {
    if (!exists(source)) {
      return;
    }
    if (isDirectory(source)) {
      createDirectories(destination);
      for (final Path child : list(source).collect(toList())) {
        copyRecursively(child, destination.resolve(child.getFileName()));
      }
    } else {
      copy(source, destination);
    }
  }
}
