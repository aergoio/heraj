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

    @Override
    public void put(Context context) {
      throw new UnsupportedOperationException();
    }
  };

  Context get();

  void put(Context context);

}
