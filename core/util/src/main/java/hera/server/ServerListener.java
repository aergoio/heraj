/*
 * @copyright defined in LICENSE.txt
 */

package hera.server;

public interface ServerListener {

  /**
   * Handle server event.
   *
   * @param event event to be occurred
   */
  void handle(ServerEvent event);
}
