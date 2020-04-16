/*
 * @copyright defined in LICENSE.txt
 */

package hera;

public interface Requester {

  <T> T request(Invocation<T> invocation) throws Exception;

}
