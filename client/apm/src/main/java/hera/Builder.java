/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static org.slf4j.LoggerFactory.getLogger;

import hera.build.Concatenator;
import hera.build.Resource;
import hera.build.ResourceManager;
import hera.build.res.BuildResource;
import hera.build.res.TestResource;
import hera.build.web.model.BuildDetails;
import java.util.Optional;
import lombok.Getter;
import org.slf4j.Logger;

public class Builder {

  protected final transient Logger logger = getLogger(getClass());

  @Getter
  protected final ResourceManager resourceManager;

  /**
   * Constructor with project file(aergo.json).
   *
   * @param resourceManager manager for resource
   *
   * @throws Exception Fail to initialize build topology
   */
  public Builder(final ResourceManager resourceManager) {
    this.resourceManager = resourceManager;
  }

  /**
   * Build {@link BuildResource} or {@link TestResource}.
   *
   * @param resourcePath basePath to resource
   *
   * @return Build result fileset
   */
  public BuildDetails build(final String resourcePath) {
    final Resource resource = resourceManager.getResource(resourcePath);
    logger.trace("{}: {}", resourcePath, resource);
    final Concatenator concatenator = new Concatenator(resourceManager);
    final Optional<BuildResource> buildResourceOpt = resource.adapt(BuildResource.class);
    final Optional<TestResource> testResourceOpt = resource.adapt(TestResource.class);
    final FileSet fileSet = new FileSet();
    if (buildResourceOpt.isPresent()) {
      return concatenator.visit(buildResourceOpt.get());
    } else if (testResourceOpt.isPresent()) {
      return concatenator.visit(testResourceOpt.get());
    }

    return new BuildDetails();
  }
}
