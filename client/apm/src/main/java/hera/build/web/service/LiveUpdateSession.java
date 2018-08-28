/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.service;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class LiveUpdateSession extends WebSocketAdapter {
  protected static LiveUpdateService manager;

  public static void setManager(final LiveUpdateService manager) {
    LiveUpdateSession.manager = manager;
  }

  @Override
  public void onWebSocketConnect(Session sess) {
    super.onWebSocketConnect(sess);
    manager.add(this);
  }

  @Override
  public void onWebSocketClose(int statusCode, String reason) {
    super.onWebSocketClose(statusCode, reason);
    manager.remove(this);
  }

  @Override
  public String toString() {
    return "WsSession[" + getSession() + "]";
  }
}
