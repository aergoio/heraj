/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.ApmConstants.PROJECT_FILENAME;
import static hera.util.FilepathUtils.getFilename;
import static hera.util.ValidationUtils.assertTrue;
import static java.util.Collections.singletonList;

import hera.Command;
import hera.ProjectFile;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateProject extends AbstractCommand implements Command {
  @Override
  public void execute() throws Exception {
    logger.trace("Starting {}...", this);
    logger.debug("Arguments: {}", arguments);
    assertTrue(0 == arguments.size());
    final Path projectPath = Paths.get(".").toAbsolutePath();
    logger.trace("Project location: {}", projectPath);

    final Path projectFilePath = Paths.get(projectPath.toString(), PROJECT_FILENAME);

    final WriteProjectFile writeProjectFile = new WriteProjectFile();
    final String projectDirectoryName = getFilename(projectPath.toFile().getAbsolutePath());
    final String projectName = System.getProperty("user.name") + "/" + projectDirectoryName;
    logger.debug("Project name: {}", projectName);
    final ProjectFile projectFile = writeProjectFile.getProject();
    projectFile.setName(projectName);
    projectFile.setSource("src/main/lua/main.lua");
    projectFile.setTarget("app.lua");
    projectFile.setEndpoint(null);
    logger.trace("Project file: {}", projectFile);
    writeProjectFile.setArguments(singletonList(projectFilePath.toAbsolutePath().toString()));
    writeProjectFile.execute();
  }
}