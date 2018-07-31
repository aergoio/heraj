/*
 * @copyright defined in LICENSE.txt
 */

package hera;

public interface Wallet {
  void bind(Context context);

  void unlock(Object authentication);

  void lock();
}
