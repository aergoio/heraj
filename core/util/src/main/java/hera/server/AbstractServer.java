/*
 * @copyright defined in LICENSE.txt
 */

package hera.server;

import static hera.server.ServerStatus.TERMINATED;
import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import lombok.Getter;
import org.slf4j.Logger;

public abstract class AbstractServer implements Server, StateChangeListener<ServerStatus> {

  protected final Logger logger = getLogger(getClass());

  /**
   * server status.
   */
  protected StateMachine<ServerStatus> state = new StateMachine<>(TERMINATED);

  /**
   * server listeners.
   */
  protected ArrayList<ServerListener> listeners = new ArrayList<>();

  /**
   * Latest exception.
   */
  @Getter
  protected Throwable exception = null;


  /**
   * Server name for track.
   */
  @Getter
  protected String name = getClass().getSimpleName();

  public AbstractServer() {
    state.addListener(this);
  }

  /* (non-Javadoc)
   * @see Server#getStatus()
   */
  @Override
  public ServerStatus getStatus() {
    return state.getState();
  }

  /* (non-Javadoc)
   * @see Server#isStatus(ServerStatus[])
   */
  @Override
  public boolean isStatus(final ServerStatus... status) {
    return state.isState(status);
  }

  /* (non-Javadoc)
   * @see Server#waitStatus(ServerStatus[])
   */
  @Override
  public void waitStatus(final ServerStatus... status) {
    state.waitState(status);
  }


  /**
   * Change status to {@code status}.
   *
   * @param status status to set
   */
  protected void changeStatus(final ServerStatus status) {
    state.changeState(status);
  }

  @Override
  public void stateChanged(final ServerStatus old, final ServerStatus state, final Object... args) {
    fireEvent(new ServerEvent(this, STATUS_CHANGED, old, state));
  }

  /**
   * Clear exception.
   */
  public void clearException() {
    this.exception = null;
  }


  /* (non-Javadoc)
   * @see Server#addServerListener(ServerListener)
   */
  @Override
  public void addServerListener(final ServerListener listener) {
    assertNotNull(listener);
    this.listeners.add(listener);
  }

  /* (non-Javadoc)
   * @see Server#removeServerListener(ServerListener)
   */
  @Override
  public void removeServerListener(final ServerListener listener) {
    this.listeners.remove(listener);
  }


  /**
   * Fire {@link ServerEvent} {@code e} to listeners.
   *
   * @param event {@link ServerEvent} to fire
   */
  protected void fireEvent(final ServerEvent event) {
    for (final ServerListener listener : this.listeners) {
      listener.handle(event);
    }
    if (listeners.isEmpty()) {
      logger.trace("Event[{}] is discarded", event);
    } else {
      logger.info("Event[{}] is fired to {} listeners", event, listeners.size());
    }
  }

  /**
   * Initialization part.
   *
   * @throws Exception Fail to initialize the server
   */
  protected void initialize() throws Exception {
  }

  /**
   * Process part.
   *
   * @throws Exception Fail to process the server task
   */
  protected void process() throws Exception {
  }

  /**
   * Exception handling part in process.
   *
   * @param th exception to be occurred
   */
  protected void handleError(final Throwable th) {
    throw new IllegalStateException(th);
  }

  /**
   * Post process part.
   * <p>
   * No exception occurred
   *
   * Just one called though exception occurred
   * </p>
   */
  protected void terminate() {
  }

}
