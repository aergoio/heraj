/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.util.ValidationUtils.assertTrue;
import static java.nio.file.Files.newBufferedReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import hera.Project;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.Setter;

public class ReadProjectFile extends AbstractCommand {
  @Getter
  @Setter
  protected Project project = new Project();

  @Override
  public void execute() throws IOException {
    assertTrue(1 == arguments.size());
    final Path projectFilePath = Paths.get(arguments.get(0));
    try (final BufferedReader reader = newBufferedReader(projectFilePath)) {
      final ObjectMapper mapper = new ObjectMapper();
      project = mapper.readValue(reader, Project.class);
    }
  }
}
