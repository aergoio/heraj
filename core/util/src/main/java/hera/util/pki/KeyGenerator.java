/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.pki;

public interface KeyGenerator {
  ECDSAKey create() throws Exception;
}
