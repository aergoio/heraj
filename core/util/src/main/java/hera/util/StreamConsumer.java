/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

public interface StreamConsumer {
  void apply(byte[] bytes, int offset, int length) throws Exception;
}
