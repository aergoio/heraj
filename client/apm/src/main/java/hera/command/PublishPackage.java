/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.ApmConstants.PROJECT_FILENAME;
import static hera.util.FilepathUtils.append;
import static hera.util.ValidationUtils.assertNotNull;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Arrays.asList;

import hera.ProjectFile;
import java.io.IOException;
import java.nio.file.Files;
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

    if (!Files.exists(Paths.get(buildTarget))) {
      new BuildProject().execute();
    }

    copy(rootProject);
  }

  protected void copy(final ProjectFile rootProject) throws IOException {
    final Path distributionSource = Paths.get(rootProject.getTarget());
    final String publishRepository = append(System.getProperty("user.home"), ".aergo_modules");
    final String publishProject = append(publishRepository, rootProject.getName());
    final Path distributionTarget = Paths.get(append(publishProject, rootProject.getTarget()));

    logger.debug("Project artifact: {}", publishProject);
    createDirectories(Paths.get(publishProject));

    Files.copy(distributionSource, distributionTarget, REPLACE_EXISTING);

    final Path aergoSource = Paths.get(PROJECT_FILENAME);
    final Path aergoTarget = Paths.get(append(publishProject, PROJECT_FILENAME));
    Files.copy(aergoSource, aergoTarget, REPLACE_EXISTING);
  }
}
