/*
 * @copyright defined in LICENSE.txt
 */

package hera;

public interface ContextProvider {

  ContextProvider defaultProvider = new ContextProvider() {
    @Override
    public Context get() {
      return EmptyContext.getInstance();
    }
  };

  Context get();

}
