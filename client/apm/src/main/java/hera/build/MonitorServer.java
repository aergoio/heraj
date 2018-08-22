/*
 * @copyright defined in LICENSE.txt
 */

package hera.build;

import hera.server.ServerStatus;
import hera.server.ThreadServer;
import hera.util.ThreadUtils;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class MonitorServer extends ThreadServer {

  protected int port = -1;

  protected Server server;

  protected final Set<Handler> handlers = new LinkedHashSet<>();

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

  public void addHandler(final Handler handler) {
    this.handlers.add(handler);
  }

  @Override
  protected void initialize() throws Exception {
    super.initialize();
    final int port = getPort();
    server = new Server(port);
    ResourceHandler resourceHandler = new ResourceHandler();

    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
    resourceHandler.setResourceBase(getClass().getResource("/public").toString());

    HandlerList handlers = new HandlerList();
    this.handlers.stream().forEach(handlers::addHandler);
    handlers.addHandler(resourceHandler);
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
    } catch (Throwable ex) {
      this.exception = ex;
    }
    super.terminate();
  }
}
