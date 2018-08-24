/*
 * @copyright defined in LICENSE.txt
 */

package hera.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerEvent {

  /**
   * Server where event occurred.
   */
  @Getter
  protected final Server server;

  /**
   * Event type.
   */
  @Getter
  protected final int type;

  /**
   * Data before event.
   */
  @Getter
  protected final Object oldData;

  /**
   * Data after event.
   */
  @Getter
  protected final Object newData;

  /**
   * Constructor with server and event type.
   *
   * @param server server
   * @param type event type
   */
  public ServerEvent(final Server server, final int type) {
    this(server, type, null);
  }

  /**
   * Constructor with server, event type, and data.
   *
   * @param server server
   * @param type event type
   * @param data data
   */
  public ServerEvent(final Server server, final int type, final Object data) {
    this(server, type, null, data);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.type + "@" + this.server + "[" + this.newData + "]";
  }
}
