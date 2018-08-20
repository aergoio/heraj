/*
 * @copyright defined in LICENSE.txt
 */

package hera.server;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerEvent {

  /**
   * Server where event occurred.
   */
  public final Server server;

  /**
   * Event type.
   */
  public final int type;

  /**
   * Data before event.
   */
  public final Object old;

  /**
   * Data after event.
   */
  public final Object data;

  /**
   * Constructor with server and event type.
   *
   * @param server server
   * @param type event type
   * @see #ServerEvent(Server, int, Object)
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
   * @see #ServerEvent(Server, int, Object, Object)
   */
  public ServerEvent(final Server server, final int type, final Object data) {
    this(server, type, null, data);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.type + "@" + this.server + "[" + this.data + "]";
  }
}
