/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.util.FilepathUtils.append;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.util.Arrays.asList;

import hera.ProjectFile;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PublishPackage extends AbstractCommand {

  @Override
  public void execute() throws Exception {
    logger.trace("Starting {}...", this);

    final ReadProjectFile readProjectFile = new ReadProjectFile();
    readProjectFile.setArguments(asList(getProjectFile().toString()));
    readProjectFile.execute();

    final ProjectFile rootProject = readProjectFile.getProject();
    final String target = rootProject.getTarget();

    final String publishRepository = append(System.getProperty("user.home"), ".aergo_modules");
    final String publishProject = append(publishRepository, rootProject.getName());
    logger.debug("Project artifact: {}", publishProject);

    new BuildProject().execute();
    if (Files.exists(Paths.get(target))) {
      createDirectories(Paths.get(publishProject));
      copy(Paths.get(target), Paths.get(append(publishProject, target)));
      copy(Paths.get("aergo.json"), Paths.get(append(publishProject, "aergo.json")));
    }
  }
}
