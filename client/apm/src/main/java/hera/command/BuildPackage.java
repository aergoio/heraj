/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.util.ValidationUtils.assertFalse;

import hera.Dependencies;
import hera.FileContent;
import hera.Project;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class BuildPackage extends AbstractCommand {

  @Getter
  @Setter
  protected Project rootProject;

  @Getter
  @Setter
  protected FileContent output;

  @Override
  public void execute() throws Exception {
    final Dependencies dependencies = new Dependencies();
    visit(rootProject, dependencies);
    final List<Project> projects = dependencies.sort();
    output = new FileContent("", () -> {

      // TODO Apply pipeline if you can
      final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      projects.stream()
          .flatMap(project -> project.getSources().stream())
          .forEach(source -> {
            try {
              byteOut.write(Files.readAllBytes(Paths.get(source)));
            } catch (final IOException e) {
              throw new IllegalStateException(e);
            }
          });
      return new ByteArrayInputStream(byteOut.toByteArray());
    });
  }

  protected void visit(
      final Project project,
      final Dependencies graph) {

    final List<Project> dependencies = project.getDependencies();
    dependencies.forEach(dependency -> visit(dependency, graph));

    if (graph.contains(project)) {
      assertFalse(graph.contains(project.getName()));
    } else {
      graph.add(project);
    }

    dependencies.forEach(dependency -> graph.addDependency(project, dependency));
  }
}
