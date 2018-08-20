/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.nio.file.Files.newInputStream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
public class ProjectFile {

  /**
   * Create {@link ProjectFile} from file basePath.
   *
   * @param path aergo.json file basePath
   *
   * @return project file
   *
   * @throws IOException Fail to read file
   */
  public static ProjectFile from(final Path path) throws IOException {
    try (final InputStream in = newInputStream(path)) {
      return ProjectFile.from(in);
    }
  }

  public static ProjectFile from(final InputStream in) throws IOException {
    final ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(in, ProjectFile.class);
  }

  @Getter
  @Setter
  protected String name;

  @Getter
  @Setter
  protected String source;

  @Getter
  @Setter
  protected String target;

  @Getter
  @Setter
  protected List<String> dependencies;

  @Getter
  @Setter
  protected List<String> tests;
}
