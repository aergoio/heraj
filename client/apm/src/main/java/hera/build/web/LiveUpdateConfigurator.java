/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web;

import static org.slf4j.LoggerFactory.getLogger;

import hera.build.web.service.LiveUpdateSessionHandler;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class LiveUpdateConfigurator implements WebSocketConfigurer {

  protected final transient Logger logger = getLogger(getClass());

  @Inject
  protected LiveUpdateSessionHandler sessionHandler;

  @Override
  public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
    registry.addHandler(sessionHandler, "/event").setAllowedOrigins("*");
    logger.info("{} registered", sessionHandler);
  }
}
