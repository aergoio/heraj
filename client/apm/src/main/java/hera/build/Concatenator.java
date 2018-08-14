/*
 * @copyright defined in LICENSE.txt
 */

package hera.build;

import static org.slf4j.LoggerFactory.getLogger;

import hera.build.res.BuildResource;
import hera.build.res.Source;
import hera.exception.BuildException;
import hera.exception.CyclicDependencyException;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class Concatenator implements ResourceVisitor {

  protected final transient Logger logger = getLogger(getClass());

  protected final Set<Resource> processing = new LinkedHashSet<>();
  protected final Set<Resource> alreadyVisits = new HashSet<>();

  protected final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

  protected final ResourceManager resourceManager;

  protected boolean needDelimiter = false;

  @Getter
  @Setter
  protected String delimiter = "\n";

  protected void visit(final BuildResource resource) {
    logger.trace("Build resource: {}", resource);
  }

  protected void visit(final Source source) {
    logger.trace("Source: {}", source);
    if (alreadyVisits.contains(source)) {
      return;
    }
    try {
      if (needDelimiter) {
        needDelimiter = false;
        buffer.write(delimiter.getBytes());
      }
      buffer.write(source.getBody().getBytes());
      needDelimiter = true;
    } catch (final Throwable e) {
      throw new BuildException(e);
    }
  }

  @Override
  public void visit(final Resource resource) {
    logger.trace("Resource: {}", resource);
    if (alreadyVisits.contains(resource)) {
      return;
    }
    if (processing.contains(resource)) {
      final StringJoiner joiner = new StringJoiner("->");
      processing.stream().map(Object::toString).forEach(joiner::add);
      joiner.add(resource.toString());
      throw new CyclicDependencyException(joiner.toString());
    }

    try {
      processing.add(resource);
      final List<Resource> dependencies = resource.getDependencies(resourceManager);
      logger.debug("Dependencies of {}: {}", resource, dependencies);
      dependencies.stream().forEach(this::visit);

      resource.adapt(Source.class).ifPresent(this::visit);
      resource.adapt(BuildResource.class).ifPresent(this::visit);
      processing.remove(resource);
      alreadyVisits.add(resource);
    } catch (final Throwable ex) {
      throw new BuildException(ex);
    }
  }

  public byte[] getResult() {
    return buffer.toByteArray();
  }
}
