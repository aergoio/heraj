/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.ApmConstants.PROJECT_FILENAME;
import static hera.util.ValidationUtils.assertTrue;
import static java.util.Collections.singletonList;

import hera.Command;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateProject extends AbstractCommand implements Command {
  @Override
  public void execute() throws Exception {
    logger.trace("Starting {}...", this);
    logger.debug("Arguments: {}", arguments);
    assertTrue(0 == arguments.size());
    final Path projectPath = Paths.get(".").toAbsolutePath();
    logger.trace("Project path: {}", projectPath);

    final Path projectFilePath = Paths.get(projectPath.toString(), PROJECT_FILENAME);

    final WriteProjectFile writeProjectFile = new WriteProjectFile();
    writeProjectFile.setArguments(singletonList(projectFilePath.toAbsolutePath().toString()));
    writeProjectFile.execute();
  }
}