/*
 * @copyright defined in LICENSE.txt
 */

package hera.build;

import static hera.util.ValidationUtils.assertNotNull;
import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;

import hera.ProjectFile;
import hera.build.web.Endpoint;
import hera.build.web.service.BuildService;
import hera.build.web.service.ConfigurationService;
import hera.build.web.service.ContractService;
import hera.server.ServerStatus;
import hera.server.ThreadServer;
import hera.util.ThreadUtils;
import java.nio.file.Path;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class MonitorServer extends ThreadServer {

  protected int port = -1;

  @Getter
  @Setter
  protected Path projectFilePath;

  protected Server server;

  @Getter
  protected ConfigurationService configurationService;

  @Getter
  protected BuildService buildService;

  @Getter
  protected ContractService contractService;

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
    assertNotNull(projectFilePath);
    configurationService = new ConfigurationService(projectFilePath);
    buildService = new BuildService();
    contractService = new ContractService();
    configurationService.getProjectFile()
        .map(ProjectFile::getEndpoint)
        .ifPresent(contractService::setEndpoint);

    final int port = getPort();
    server = new Server(port);
    final ServletContextHandler context = new ServletContextHandler(SESSIONS);
    context.setContextPath("/");
    context.setResourceBase(getClass().getResource("/public").toString());
    final Endpoint endpoint = new Endpoint();
    endpoint.setConfigurationService(configurationService);
    endpoint.setBuildService(buildService);
    endpoint.setContractService(contractService);
    context.addServlet(new ServletHolder(endpoint), "/");

    final HandlerList handlers = new HandlerList();
    handlers.addHandler(context);
    handlers.addHandler(new DefaultHandler());
    server.setHandler(handlers);
    server.start();
    logger.info("Open browser and connect to http://localhost:{}", port);
  }

  @Override
  protected void process() throws Exception {
    super.process();
    ThreadUtils.trySleep(2000);
  }

  @Override
  protected void terminate() {
    try {
      server.stop();
      server.join();
    } catch (Throwable ex) {
      this.exception = ex;
    }
    super.terminate();
  }
}
