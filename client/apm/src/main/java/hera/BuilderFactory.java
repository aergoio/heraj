/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.build.ResourceManager;
import hera.build.res.Project;

public class BuilderFactory {

  /**
   * Create builder for {@code project}.
   *
   * @param project project of builder
   *
   * @return builder to create
   */
  public Builder create(final Project project) {
    final ResourceManager resourceManager = new ResourceManager(project);
    Builder builder = new Builder(resourceManager);
    return builder;
  }
}
