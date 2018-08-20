/*
 * @copyright defined in LICENSE.txt
 */

package hera.server;

public class StateConditionFactory {

  public static <StateT> StateCondition<StateT> when(final StateCondition<StateT> condition) {
    return condition;
  }

  @SuppressWarnings({
      "unchecked", "rawtypes"
  })
  public static <StateT> StateCondition<StateT> when(final StateT... condition) {
    return (state) -> state.isState(condition);
  }

  @SuppressWarnings({
      "unchecked", "rawtypes"
  })
  public static <StateT> StateCondition<StateT> not(final StateT... states) {
    return (state) -> !state.isState(states);
  }
}
