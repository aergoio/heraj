/*
 * @copyright defined in LICENSE.txt
 */

package hera.server;

public class StateConditionFactory {

  public static <StateT> StateCondition<StateT> when(final StateCondition<StateT> condition) {
    return condition;
  }

  /**
   * Return a condition evaluator which returns true when {@code conditions}... matches.
   *
   * @param <StateT> a state type
   * @param conditions conditions to match
   * @return a condition evaluator
   */
  @SuppressWarnings("unchecked")
  public static <StateT> StateCondition<StateT> when(final StateT... conditions) {
    return new StateCondition<StateT>() {
      @Override
      public boolean evaluate(StateMachine<StateT> state) {
        return state.isState(conditions);
      }
    };
  }


  /**
   * Return a condition evaluator which returns false when {@code states}... matches.
   *
   * @param <StateT> a state type
   * @param states states to match
   * @return a condition evaluator
   */
  @SuppressWarnings("unchecked")
  public static <StateT> StateCondition<StateT> not(final StateT... states) {
    return new StateCondition<StateT>() {
      @Override
      public boolean evaluate(StateMachine<StateT> state) {
        return !state.isState(states);
      }
    };
  }
}
