/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.ApmConstants.MODULES_BASE;
import static hera.ApmConstants.PROJECT_FILENAME;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Command;
import hera.ProjectFile;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;

public abstract class AbstractCommand implements Command {

  protected final transient Logger logger = getLogger(getClass());

  @Getter
  @Setter
  protected List<String> arguments = emptyList();

  public String getArgument(final int index) {
    return arguments.get(index);
  }

  /**
   * Get {@code index} th argument.
   *
   * @param index argument index
   *
   * @return argument if exists
   */
  public Optional<String> getOptionalArgument(int index) {
    if (index < 0) {
      return empty();
    }
    if (index < arguments.size()) {
      return ofNullable(arguments.get(index));
    } else {
      return empty();
    }
  }

  public Path getProjectHome() {
    return Paths.get(".");
  }

  public Path getProjectFile() {
    return Paths.get(getProjectHome().toString(), PROJECT_FILENAME);
  }

  public Path getModuleBasePath() {
    return Paths.get(getProjectHome().toString(), MODULES_BASE);
  }


  /**
   * Read project file.
   *
   * @return project
   *
   * @throws IOException On failure to read project file
   */
  public ProjectFile readProject() throws IOException {
    ReadProjectFile readProjectFile = new ReadProjectFile();
    readProjectFile.setArguments(asList(getProjectFile().toString()));
    readProjectFile.execute();
    return readProjectFile.getProject();
  }

}
