/*
 * @copyright defined in LICENSE.txt
 */

package hera.server;

public class StateConditionFactory {

  public static <StateT> StateCondition<StateT> when(final StateCondition<StateT> condition) {
    return condition;
  }

  public static <StateT> StateCondition<StateT> when(final StateT... condition) {
    return new StateCondition<StateT>() {
      @Override
      public boolean evaluate(StateMachine<StateT> state) {
        return state.isState(condition);
      }
    };
  }

  public static <StateT> StateCondition<StateT> not(final StateT... states) {
    return new StateCondition<StateT>() {
      @Override
      public boolean evaluate(StateMachine<StateT> state) {
        return !state.isState(states);
      }
    };
  }
}
