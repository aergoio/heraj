/*
 * @copyright defined in LICENSE.txt
 */

package hera.server;

public interface StateChangeListener<StateT> {

  void stateChanged(StateT from, StateT to, Object... args);
}
