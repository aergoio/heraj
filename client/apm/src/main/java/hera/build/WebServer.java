/*
 * @copyright defined in LICENSE.txt
 */

package hera.build;

import static hera.DefaultConstants.DEFAULT_ENDPOINT;
import static hera.util.StringUtils.nvl;

import hera.ProjectFile;
import hera.build.web.SpringWebLauncher;
import hera.build.web.service.BuildService;
import hera.server.ServerStatus;
import hera.server.ThreadServer;
import hera.util.ThreadUtils;
import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@NoArgsConstructor
public class WebServer extends ThreadServer {

  protected int port = -1;

  @Setter
  protected ProjectFile projectFile;

  protected ConfigurableApplicationContext applicationContext;

  public WebServer(final int port) {
    this.port = port;
  }

  /**
   * Get server port.
   * <p>
   * Return 8080 as default port if not specified.
   * </p>
   * @return server port
   */
  public int getPort() {
    if (port < 0) {
      return 8080;
    } else {
      return port;
    }
  }

  /**
   * Get build service from server.
   *
   * @return build service
   */
  public BuildService getBuildService() {
    if (null == applicationContext) {
      return null;
    }
    return applicationContext.getBean(BuildService.class);
  }

  /**
   * Set server port.
   * <p>
   * Specify negative value if you want default port.
   * </p>
   *
   * @param port server port
   */
  public void setPort(final int port) {
    if (isStatus(ServerStatus.TERMINATED)) {
      this.port = port;
    } else {
      throw new IllegalStateException("Server already started");
    }
  }

  @Override
  protected void initialize() throws Exception {
    super.initialize();
    final SpringApplicationBuilder builder = new SpringApplicationBuilder(SpringWebLauncher.class);
    builder.bannerMode(Mode.OFF);
    builder.logStartupInfo(false);

    final Map<String, Object> properties = new HashMap<>();
    if (0 <= port) {
      properties.put("server.port", port);
    }
    properties.put("project.endpoint", nvl(projectFile.getEndpoint(), DEFAULT_ENDPOINT));
    if (!properties.isEmpty()) {
      builder.properties(properties);
    }
    applicationContext = builder.run();
  }

  @Override
  protected void process() throws Exception {
    super.process();
    ThreadUtils.trySleep(2000);
  }

  @Override
  protected void terminate() {
    try {
      applicationContext.close();;
    } catch (Throwable ex) {
      this.exception = ex;
    }
    super.terminate();
  }
}
