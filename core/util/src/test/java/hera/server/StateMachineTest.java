package hera.server;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;

import hera.AbstractTestCase;
import java.util.concurrent.Callable;
import org.junit.Test;

public class StateMachineTest extends AbstractTestCase {
  @Test
  @SuppressWarnings("unchecked")
  public void testExecute() {
    final String s1 = randomUUID().toString();
    final String success = randomUUID().toString();
    final String failure = randomUUID().toString();
    final Callable<Void> callable1 = () -> {
      logger.trace("Success");
      return null;
    };
    final Callable<Void> callable2 = () -> {
      throw new IllegalArgumentException();
    };
    final StateChangeListener<String> listener = mock(StateChangeListener.class);
    final StateMachine<String> stateMachine = new StateMachine<>(s1);
    stateMachine.addListener(listener);
    stateMachine.execute(callable1, success, failure);
    assertEquals(success, stateMachine.getState());
    stateMachine.execute(callable2, success, failure);
    assertEquals(failure, stateMachine.getState());
    stateMachine.removeListener(listener);
  }

  @Test
  public void testToString() {
    final String s1 = randomUUID().toString();
    final StateMachine<String> stateMachine = new StateMachine<>(s1);
    assertTrue(stateMachine.toString().contains(s1));
  }

}