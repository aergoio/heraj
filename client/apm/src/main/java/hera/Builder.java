package hera;

import static org.slf4j.LoggerFactory.getLogger;

import hera.build.Dependency;
import hera.build.Resource;
import hera.build.Source;
import hera.exception.CyclicDependencyException;
import hera.util.Graph;
import hera.util.IoUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class Builder {

  protected final transient Logger logger = getLogger(getClass());

  protected final ProjectFile project;

  protected final Source source;

  protected final Graph<Resource, Dependency> graph = new Graph<>();

  /**
   * Constructor with project file(aergo.json).
   *
   * @param project project file
   *
   * @throws Exception Fail to initialize build topology
   */
  public Builder(final ProjectFile project) throws Exception {
    this.project = project;
    source = new Source(project, project.getSource());

    this.add(source);
  }

  /**
   * Add resource for builder to manage.
   *
   * @param resource resource to manage
   *
   * @throws Exception Fail to add resource
   */
  protected void add(final Resource resource) throws Exception {
    graph.add(resource);
    for (final Resource target : resource.getDependencies()) {
      this.add(target);
      graph.add(new Dependency("use", resource, target));
    }
  }

  /**
   * Build project.
   *
   * @throws Exception Fail to build project
   */
  public void build() throws Exception {
    logger.trace("Starting build...");
    final List<Resource> buildSequence = graph.inverse().sort();
    logger.debug("Build sequence: {}", buildSequence);
    if (null == buildSequence) {
      throw new CyclicDependencyException();
    }
    if (buildSequence.isEmpty()) {
      return;
    }

    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    for (final Resource resource : buildSequence) {
      if (resource instanceof Source) {
        final Source source = (Source) resource;
        source.getBody().read(() -> byteOut);
      } else {
        throw new IllegalStateException();
      }
    }

    final String target = project.getTarget();
    try (final OutputStream out = Files.newOutputStream(Paths.get(target))) {
      IoUtils.redirect(new ByteArrayInputStream(byteOut.toByteArray()), out);
    }
  }
}
