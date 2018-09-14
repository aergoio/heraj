/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.res;

import static hera.util.FilepathUtils.append;
import static hera.util.FilepathUtils.getParentPath;
import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.build.Resource;
import hera.build.ResourceManager;
import hera.exception.BuildException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class Source extends File {

  protected final transient Logger logger = getLogger(getClass());

  public Source(final Project project, final String location) {
    super(project, location);
  }

  @Override
  public List<Resource> getDependencies(ResourceManager resourceManager) throws Exception {
    final ArrayList<Resource> dependencies = new ArrayList<>();
    dependencies.addAll(super.getDependencies(resourceManager));
    readImports().stream()
        .map(importPath -> this.bind(resourceManager, importPath))
        .forEach(dependencies::add);
    return dependencies;
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

  /**
   * Read import clause and convert to import target.
   *
   * @return import target list
   *
   * @throws IOException Fail to read source file.
   */
  public List<String> readImports() throws IOException {
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

  protected Resource bind(final ResourceManager resourceManager, final String importPath) {
    if (importPath.startsWith("./") || importPath.startsWith("../")) {
      return resourceManager.getResource(append(getParentPath(getLocation()), importPath));
    } else {
      return resourceManager.getPackage(importPath);
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
          logger.trace("Line: {}", line);
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
}
