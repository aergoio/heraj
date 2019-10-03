/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.pki;

public interface KeyGenerator<KeyT> {
  KeyT create() throws Exception;

  KeyT create(String seed) throws Exception;
}
