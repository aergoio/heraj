/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.util.ObjectUtils.nvl;
import static java.util.Collections.EMPTY_LIST;

import hera.Builder;
import hera.FileContent;
import hera.FileSet;
import hera.ProjectFile;
import hera.build.res.Project;
import hera.test.LuaRunner;
import hera.util.IoUtils;
import java.io.InputStream;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class TestProject extends AbstractCommand {

  @Getter
  @Setter
  protected BuilderFactory builderFactory = new BuilderFactory();

  @Override
  public void execute() throws Exception {
    logger.trace("Starting {}...", this);
    final ProjectFile projectFile = readProject();
    final Project project = new Project(".", projectFile);
    final Builder builder = new BuilderFactory().create(project);

    final List<String> testPaths = nvl(projectFile.getTests(), EMPTY_LIST);
    for (final String testPath : testPaths) {

      final FileSet fileSet = builder.build(testPath);
      logger.trace("Test build: {}", fileSet);
      final FileContent fileContent = fileSet.stream().findFirst().orElse(null);
      try (final InputStream in = fileContent.open()) {
        final String script = new String(IoUtils.from(in));
        new LuaRunner().run(script);
      }
    }
  }
}
