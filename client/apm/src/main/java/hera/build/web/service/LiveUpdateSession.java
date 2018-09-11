/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.service;

import java.io.IOException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@RequiredArgsConstructor
public class LiveUpdateSession {

  @Getter
  @NonNull
  protected final WebSocketSession session;

  public void sendMessage(final String message) throws IOException {
    TextMessage textMessage = new TextMessage(message);
    session.sendMessage(textMessage);
  }

  @Override
  public int hashCode() {
    return session.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof  LiveUpdateSession)) {
      return false;
    }
    final LiveUpdateSession other = (LiveUpdateSession) obj;
    return this.session.equals(other.session);
  }
}
