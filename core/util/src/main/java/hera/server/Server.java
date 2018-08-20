/*
 * @copyright defined in LICENSE.txt
 */

package hera.server;

public interface Server {

  int STATUS_CHANGED = 1;

  /**
   * Add a listener for server event.
   *
   * @param listener listener to add
   */
  void addServerListener(ServerListener listener);

  /**
   * Remove listener to be registered.
   *
   * @param listener listener to remove
   */
  void removeServerListener(ServerListener listener);

  /**
   * Return server status.
   *
   * @return current server status
   */
  ServerStatus getStatus();


  /**
   * Check if current status is one of {@code status}.
   *
   * @param status status values to check
   * @return if current status is one of {@code status}
   */
  boolean isStatus(final ServerStatus... status);

  /**
   * Wait for server status to be one of {@code status}.
   *
   * @param status status values to wait for
   */
  void waitStatus(final ServerStatus... status);

  /**
   * Start server.
   *
   * @throws IllegalStateException If already started
   */
  void boot();

  /**
   * Stop server.
   *
   * @throws IllegalStateException If already terminate
   */
  void down();
}
