/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.util.ValidationUtils.assertFalse;
import static hera.util.ValidationUtils.assertTrue;
import static java.nio.file.Files.newBufferedWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import hera.ProjectFile;
import java.io.BufferedWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.Setter;

public class WriteProjectFile extends AbstractCommand {

  @Getter
  @Setter
  protected ProjectFile project = new ProjectFile();

  @Override
  public void execute() throws Exception {
    assertTrue(1 == arguments.size());
    final Path projectFilePath = Paths.get(arguments.get(0));
    final ObjectMapper mapper = new ObjectMapper();
    try (final BufferedWriter writer = newBufferedWriter(projectFilePath)) {
      mapper.writerWithDefaultPrettyPrinter().writeValue(writer, project);
    }
  }
}
