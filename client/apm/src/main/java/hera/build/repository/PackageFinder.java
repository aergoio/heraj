package hera.build.repository;

import static hera.ApmConstants.PROJECT_FILENAME;
import static hera.util.FilepathUtils.append;
import static java.nio.file.Files.newBufferedReader;
import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.databind.ObjectMapper;
import hera.ProjectFile;
import hera.build.ResourceFinder;
import hera.build.dep.PackageImport;
import hera.build.res.Project;
import java.io.BufferedReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;

public class PackageFinder implements ResourceFinder<PackageImport, Project> {

  protected final Logger logger = getLogger(getClass());

  @Override
  public Project find(final PackageImport dependency) {
    logger.trace("Import: {}", dependency);
    try {
      final String publishRepository = append(System.getProperty("user.home"), ".aergo_modules");
      final String projectPath = append(publishRepository, dependency.getPackageName());
      final Path projectFilePath = Paths.get(append(projectPath, PROJECT_FILENAME));
      try (final BufferedReader reader = newBufferedReader(projectFilePath)) {
        final ObjectMapper mapper = new ObjectMapper();
        final ProjectFile projectFile = mapper.readValue(reader, ProjectFile.class);
        return new Project(projectPath, projectFile);
      }
    } catch (final Throwable ex) {
      return new Project(null, null);
    }
  }
}
