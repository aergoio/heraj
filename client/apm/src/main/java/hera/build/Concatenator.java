/*
 * @copyright defined in LICENSE.txt
 */

package hera.build;

import static org.slf4j.LoggerFactory.getLogger;

import hera.build.res.Source;
import hera.build.web.model.BuildDependency;
import hera.build.web.model.BuildDetails;
import hera.exception.BuildException;
import hera.exception.CyclicDependencyException;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class Concatenator {

  protected final transient Logger logger = getLogger(getClass());

  protected final Set<Resource> processing = new LinkedHashSet<>();

  @Getter
  protected final ResourceManager resourceManager;

  @Getter
  protected final Set<Resource> visitedResources;

  public Concatenator(final ResourceManager resourceManager) {
    this(resourceManager, new LinkedHashSet<>());
  }

  @Getter
  @Setter
  protected String delimiter = "\n";

  protected byte[] visit(final Source source) {
    if (visitedResources.contains(source)) {
      return null;
    }
    try {
      return source.getBody().getBytes();
    } catch (final Throwable e) {
      throw new BuildException(e);
    }
  }

  /**
   * Visit resource for concatenation.
   *
   * @param resource resource to visit
   *
   * @return concatenated result
   */
  public BuildDetails visit(final Resource resource) {
    final BuildDependency dependencyRoot = new BuildDependency();
    dependencyRoot.setName(resource.getLocation());
    byte[] contents = visit(resource, dependencyRoot);

    final BuildDetails buildDetails = new BuildDetails();
    buildDetails.setResult(contents);
    buildDetails.setDependencies(dependencyRoot);
    return buildDetails;
  }

  /**
   * Visit resource with dependency.
   *
   * @param resource resource to concatenate
   * @param resourceDependency object to record dependencies
   *
   * @return concatenated bytes
   */
  public byte[] visit(final Resource resource, final BuildDependency resourceDependency) {
    logger.trace("Resource: {}", resource);
    if (visitedResources.contains(resource)) {
      return null;
    }
    if (processing.contains(resource)) {
      final StringJoiner joiner = new StringJoiner("->");
      processing.stream().map(Object::toString).forEach(joiner::add);
      joiner.add(resource.toString());
      throw new CyclicDependencyException(joiner.toString());
    }

    boolean needsDelimiter = false;
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

    try {
      processing.add(resource);

      final Optional<ResourceManager> resouceManagerOpt = resource.adapt(ResourceManager.class);
      final Concatenator next = resouceManagerOpt
          .map(newResourceManager -> new Concatenator(newResourceManager, visitedResources))
          .orElse(this);
      if (this != next) {
        logger.info("Resource manager changed: {} -> {}", this, next);
      }
      final ResourceManager nextResourceManager = next.getResourceManager();
      final List<Resource> dependencies = resource.getDependencies(nextResourceManager);
      logger.debug("Dependencies of {}: {}", resource, dependencies);
      for (final Resource dependency : dependencies) {
        final BuildDependency childDependency = new BuildDependency();
        childDependency.setName(dependency.getLocation());
        final byte[] contents = next.visit(dependency, childDependency);
        resourceDependency.add(childDependency);
        if (null == contents) {
          continue;
        }
        if (needsDelimiter) {
          byteOut.write("\n".getBytes());
        }
        byteOut.write(contents);
        needsDelimiter = true;
      }
      final byte[] contents = resource.adapt(Source.class).map(next::visit).orElse(null);
      if (null != contents) {
        if (needsDelimiter) {
          byteOut.write("\n".getBytes());
        }
        byteOut.write(contents);
        needsDelimiter = true;
      }

      processing.remove(resource);
      visitedResources.add(resource);
      if (needsDelimiter) {
        return byteOut.toByteArray();
      } else {
        return null;
      }
    } catch (final Throwable ex) {
      throw new BuildException(ex);
    }
  }
}
