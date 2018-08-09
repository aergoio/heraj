/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static java.util.Arrays.asList;

import hera.Builder;
import hera.FileContent;
import hera.ProjectFile;
import lombok.Getter;
import lombok.Setter;

public class BuildPackage extends AbstractCommand {

  @Getter
  @Setter
  protected FileContent output;

  @Override
  public void execute() throws Exception {
    logger.trace("Starting {}...", this);

    final ReadProjectFile readProjectFile = new ReadProjectFile();
    readProjectFile.setArguments(asList(getProjectFile().toString()));
    readProjectFile.execute();

    final ProjectFile rootProject = readProjectFile.getProject();
    final Builder builder = new Builder(rootProject);
    builder.build();
  }
}
