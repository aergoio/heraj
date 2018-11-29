/*
 * @copyright defined in LICENSE.txt
 */

package hera;

@FunctionalInterface
public interface ContextProvider {

  ContextProvider defaultProvider = () -> EmptyContext.getInstance();

  Context get();

}
