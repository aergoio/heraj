/*
 * @copyright defined in LICENSE.txt
 */

package hera.server;

public interface StateCondition<StateT> {

  boolean evaluate(StateMachine<StateT> state);
}
