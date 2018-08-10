package hera.build.repository;

import static org.slf4j.LoggerFactory.getLogger;

import hera.ProjectFile;
import hera.build.Resource;
import hera.build.ResourceFinder;
import hera.build.dep.SourceImport;
import hera.build.res.Project;
import hera.build.res.Source;
import java.nio.file.Path;
import org.slf4j.Logger;

public class SourceFinder implements ResourceFinder<SourceImport, Source> {
  protected final Logger logger = getLogger(getClass());

  @Override
  public Source find(final SourceImport dependency) {
    logger.trace("Finding {}...", dependency);
    final Resource source = dependency.getSource();
    final Path sourcePath = source.getPath();
    logger.trace("Source path: {}", sourcePath);
    final Project project = source.adapt(Project.class).get();
    final Path importedSourcePath =
        project.getPath().relativize(sourcePath.getParent().resolve(dependency.getPath()));
    logger.trace("Imported source path: {}", importedSourcePath);

    final Source luaSource =
        new Source(source.adapt(Project.class).get(), importedSourcePath.toString());
    logger.trace("{} found", luaSource);
    return luaSource;
  }

  public Path get(final Source source) {
    return source.getPath();
  }
}
