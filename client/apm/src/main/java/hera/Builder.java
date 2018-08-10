package hera;

import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;

import hera.build.Dependency;
import hera.build.Resource;
import hera.build.ResourceDependency;
import hera.build.ResourceFinder;
import hera.build.dep.BuildEntry;
import hera.build.dep.PackageImport;
import hera.build.dep.SourceImport;
import hera.build.repository.PackageFinder;
import hera.build.repository.SourceFinder;
import hera.build.res.Project;
import hera.build.res.Source;
import hera.exception.CyclicDependencyException;
import hera.util.Graph;
import hera.util.IoUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;

public class Builder {

  protected final transient Logger logger = getLogger(getClass());

  protected final Graph<Resource, Dependency> graph = new Graph<>();

  protected final Project project;

  @Getter
  @Setter
  protected ResourceFinder<SourceImport, Source> sourceFinder = new SourceFinder();

  @Getter
  @Setter
  protected ResourceFinder<PackageImport, Project> packageFinder = new PackageFinder();

  /**
   * Constructor with project file(aergo.json).
   *
   * @param project project file
   *
   * @throws Exception Fail to initialize build topology
   */
  public Builder(final Project project) throws Exception {
    this.project = project;
    this.add(project);
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
    for (final ResourceDependency dependency : resource.getDependencies()) {
      if (dependency instanceof SourceImport) {
        final Source source = sourceFinder.find((SourceImport) dependency);
        add(source);
        graph.add(new Dependency("use", resource, source));
      } else if (dependency instanceof PackageImport) {
        final Project packageProject = packageFinder.find((PackageImport) dependency);
        add(packageProject);
        graph.add(new Dependency("import-project", resource, packageProject));
      } else if (dependency instanceof BuildEntry) {
        final BuildEntry entry = (BuildEntry) dependency;
        final Source source = new Source(entry.getProject(), entry.getTarget());
        add(source);
        graph.add(new Dependency("entry-of-project", resource, source));
      }
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
    if (logger.isDebugEnabled()) {

      logger.debug("Build sequence:\n{}",
          buildSequence.stream().map(obj -> "\t" + obj).collect(joining("\n")));
    }
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
      } else if (resource instanceof Project) {
        final Project apmPackage = (Project) resource;
      } else {
        throw new IllegalStateException();
      }
    }

    final String target = project.getProjectFile().getTarget();
    try (final OutputStream out = Files.newOutputStream(Paths.get(target))) {
      IoUtils.redirect(new ByteArrayInputStream(byteOut.toByteArray()), out);
    }
  }
}
