/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.service;

import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Named
public class LiveUpdateSessionHandler extends TextWebSocketHandler {

  @Inject
  protected LiveUpdateService manager;

  @Override
  public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
    super.afterConnectionEstablished(session);
    manager.add(new LiveUpdateSession(session));
  }

  @Override
  public void afterConnectionClosed(
      final WebSocketSession session, final CloseStatus status) throws Exception {
    manager.remove(new LiveUpdateSession(session));
    super.afterConnectionClosed(session, status);
  }

}
