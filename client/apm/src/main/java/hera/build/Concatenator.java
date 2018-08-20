/*
 * @copyright defined in LICENSE.txt
 */

package hera.build;

import static org.slf4j.LoggerFactory.getLogger;

import hera.build.res.Source;
import hera.exception.BuildException;
import hera.exception.CyclicDependencyException;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
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

  protected final Set<Resource> alreadyVisits;

  public Concatenator(final ResourceManager resourceManager) {
    this(resourceManager, new HashSet<>());
  }

  @Getter
  @Setter
  protected String delimiter = "\n";

  protected byte[] visit(final Source source) {
    if (alreadyVisits.contains(source)) {
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
  public byte[] visit(final Resource resource) {
    logger.trace("Resource: {}", resource);
    if (alreadyVisits.contains(resource)) {
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
      final Concatenator nextConcatenator = resouceManagerOpt
          .map(newResourceManager -> new Concatenator(newResourceManager, alreadyVisits))
          .orElse(this);
      if (this != nextConcatenator) {
        logger.info("Resource manager changed: {} -> {}", this, nextConcatenator);
      }
      final ResourceManager nextResourceManager = nextConcatenator.getResourceManager();
      final List<Resource> dependencies = resource.getDependencies(nextResourceManager);
      logger.debug("Dependencies of {}: {}", resource, dependencies);
      for (final Resource dependency : dependencies) {
        byte[] contents = nextConcatenator.visit(dependency);
        if (null == contents) {
          continue;
        }
        if (needsDelimiter) {
          byteOut.write("\n".getBytes());
        }
        byteOut.write(contents);
        needsDelimiter = true;
      }
      dependencies.stream().map(nextConcatenator::visit);
      byte[] contents = resource.adapt(Source.class).map(nextConcatenator::visit).orElse(null);
      if (null != contents) {
        if (needsDelimiter) {
          byteOut.write("\n".getBytes());
        }
        byteOut.write(contents);
        needsDelimiter = true;
      }

      processing.remove(resource);
      alreadyVisits.add(resource);
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
