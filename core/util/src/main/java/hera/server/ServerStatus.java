/*
 * @copyright defined in LICENSE.txt
 */

package hera.server;

public enum ServerStatus {
  TERMINATED,
  BOOTING,
  INITIALIZING,
  PROCESSING,
  SKIP,
  DOWNING,
  TERMINATING
}
