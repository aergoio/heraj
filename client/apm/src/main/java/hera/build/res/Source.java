/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.res;

import static hera.util.FilepathUtils.getParentPath;
import static hera.util.ObjectUtils.equal;
import static hera.util.ValidationUtils.assertTrue;
import static java.nio.file.Files.newBufferedReader;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;

import hera.ProjectFile;
import hera.build.Resource;
import hera.build.ResourceDependency;
import hera.build.dep.PackageImport;
import hera.build.dep.SourceImport;
import hera.util.FilepathUtils;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;

@ToString(exclude = {"logger"})
public class Source implements Resource {

  protected final transient Logger logger = getLogger(getClass());

  @Getter
  @Setter
  protected Project project;

  @Getter
  @Setter
  protected String location;

  public Source() {
  }

  public Source(final Project project, final String path) {
    this.project = project;
    this.location = path;
  }

  public Path getPath() {
    return Paths.get(project.getLocation(), location);
  }

  @Override
  public <T> Optional<T> adapt(Class<T> adaptor) {
    if (adaptor.isInstance(project)) {
      return (Optional<T>) ofNullable(project);
    }
    return Optional.empty();
  }

  public BufferedReader open() throws IOException {
    return newBufferedReader(getPath());
  }

  public List<ResourceDependency> getDependencies() throws IOException {
    return readImports().stream().map(this::bind).collect(Collectors.toList());
  }

  protected String fromImport(final String line) {
    final String importPrefix = "import";
    if (!line.startsWith(importPrefix)) {
      return null;
    }
    if (!Character.isWhitespace(line.charAt(importPrefix.length()))) {
      return null;
    }
    final String literals = line.substring(importPrefix.length()).trim();
    if (!literals.startsWith("\"") || !literals.endsWith("\"")) {
      return null;
    }
    return literals.substring(1, literals.length() - 1).trim();
  }

  protected List<String> readImports() throws IOException {
    final List<String> imports = new ArrayList<>();
    try (final BufferedReader sourceIn = open()) {
      // Read line by line
      String line = null;
      while (null != (line = sourceIn.readLine())) {
        final String importStr = fromImport(line);
        if (null == importStr) {
          return imports;
        }
        imports.add(importStr);
      }
      return imports;
    } catch (final NoSuchFileException e) {
      logger.trace("{} not found", location);
      return emptyList();
    }
  }

  protected ResourceDependency bind(final String target) {
    logger.trace("Target: {}", target);
    if (target.startsWith("./") || target.startsWith("../")) {
      return new SourceImport(this, target);
    } else {
      final Collection<String> packageDependencies = project.getProjectFile().getDependencies();
      assertTrue(packageDependencies.contains(target),
          "Project doesn't include " + target + ": " + packageDependencies);
      return new PackageImport(this, target);
    }
  }

  /**
   * Return text without upper import part.
   *
   * @return body text
   */
  public Text getBody() {
    return new Text(() -> {
      try (final BufferedReader sourceIn = open()) {
        // Read line by line
        String line = null;
        boolean isBodyPart = false;
        final StringWriter writer = new StringWriter();
        while (null != (line = sourceIn.readLine())) {
          if (isBodyPart) {
            writer.write("\n");
            writer.write(line);
          } else {
            final String importStr = fromImport(line);
            if (null == importStr) {
              writer.write(line);
              isBodyPart = true;
            }
          }
        }
        return new ByteArrayInputStream(writer.toString().getBytes());
      }
    });
  }

  @Override
  public int hashCode() {
    return project.hashCode() + location.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof Source)) {
      return false;
    }
    final Source that = (Source) obj;
    if (!equal(this.project, that.project)) {
      return false;
    }

    if (!equal(this.location, that.location)) {
      return false;
    }
    return true;
  }
}
