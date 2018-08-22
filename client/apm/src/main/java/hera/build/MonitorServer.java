/*
 * @copyright defined in LICENSE.txt
 */

package hera.build;

import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;

import hera.build.web.Endpoint;
import hera.build.web.service.BuildService;
import hera.server.ServerStatus;
import hera.server.ThreadServer;
import hera.util.ThreadUtils;
import lombok.Getter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class MonitorServer extends ThreadServer {

  protected int port = -1;

  protected Server server;

  @Getter
  protected BuildService buildService = new BuildService();

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
    final int port = getPort();
    server = new Server(port);
    ServletContextHandler context = new ServletContextHandler(SESSIONS);
    context.setContextPath("/");
    context.setResourceBase(getClass().getResource("/public").toString());
    final Endpoint endpoint = new Endpoint();
    endpoint.setBuildService(buildService);
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
