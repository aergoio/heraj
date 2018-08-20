/*
 * @copyright defined in LICENSE.txt
 */

package hera.server;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;

public class StateMachine<StateT> {

  protected final Logger logger = getLogger(getClass());

  protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  protected Collection<StateChangeListener<StateT>> listeners = new LinkedHashSet<>();

  protected StateT state;

  protected Object[] args;

  public StateMachine(final StateT initialState) {
    super();
    this.state = initialState;
  }

  public ReadWriteLock getLock() {
    return lock;
  }

  public void addListener(final StateChangeListener<StateT> listener) {
    this.listeners.add(listener);
  }

  public void removeListener(final StateChangeListener<StateT> listener) {
    this.listeners.remove(listener);
  }

  /**
   * Check if current state is one of {@code states}.
   *
   * @param states states to check
   *
   * @return if current state is one of {@code states}
   */
  @SuppressWarnings("unchecked")
  public boolean isState(final StateT... states) {
    StateT temp = null;
    lock.readLock().lock();
    try {
      temp = this.state;
    } finally {
      lock.readLock().unlock();
    }

    for (final StateT st : states) {

      if (temp == st) {
        return true;
      }
    }

    return false;
  }

  /**
   * Wait that current state is one of {@code states}.
   *
   * @param states states to wait for
   */
  @SuppressWarnings("unchecked")
  public void waitState(final StateT... states) {
    while (true) {
      boolean isLocked = false;
      lock.readLock().lock();
      try {
        isLocked = true;

        if (isState(states)) {
          return;
        }
        synchronized (this) {
          lock.readLock().unlock();
          isLocked = false;
          try {
            wait();
          } catch (final InterruptedException e) {
            logger.warn("Interrupt waiting");
          }
        }
      } finally {
        if (isLocked) {
          lock.readLock().unlock();
        }
      }
    }
  }

  /**
   * Return current state.
   *
   * @return current state
   */
  public StateT getState() {
    lock.readLock().lock();
    try {
      return this.state;
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Get arguments.
   *
   * @return current state's arguments
   */
  public Object[] getArguments() {
    lock.readLock().lock();

    try {
      return this.args;
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Change current status to {@code state}.
   * <p>
   * Use lock for write.
   * </p>
   *
   * @param state state to change
   * @param args arguments which next state have
   */
  public void changeState(final StateT state, final Object... args) {
    lock.writeLock().lock();
    try {
      final StateT old = this.state;
      this.state = state;
      this.args = args;
      logger.info("Status: {} -> {}", old, this.state);
      synchronized (this) {
        notifyAll();
      }
      fireChangeEvent(old, this.state, args);
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * Change current state to {@code state} if condition is true.
   * <p>
   * Use lock for write
   * </p>
   *
   * @param state     state to change
   * @param condition condition to check
   * @param args      arguments which next state have
   */
  public boolean changeState(final StateT state, final StateCondition<StateT> condition,
      final Object... args) {
    lock.writeLock().lock();
    try {
      if (!condition.evaluate(this)) {
        return false;
      }
      changeState(state);
      return true;
    } finally {
      lock.writeLock().unlock();
    }
  }


  /**
   * Execute and change state as the result.
   *
   * @param process {@link Callable} to execute
   * @param success state to transit when succeed
   * @param fail    state to transit when fail
   */
  public void execute(final Callable<Void> process, final StateT success, final StateT fail) {
    lock.writeLock().lock();
    try {
      process.call();
      logger.trace("Success to execute {}", process);
      if (null != success) {
        changeState(success);
      }
    } catch (Throwable th) {
      if (null != fail) {
        logger.trace("Fail to execute {}", process);
        changeState(fail);
      }
    } finally {
      lock.writeLock().unlock();
    }
  }

  protected void fireChangeEvent(final StateT from, final StateT to, final Object... args) {
    for (final StateChangeListener<StateT> listener : listeners) {
      try {
        listener.stateChanged(from, to, args);
      } catch (final Throwable e) {
        logger.debug("Unexpected exception in listener", e);
      }
    }
  }

  @Override
  public String toString() {
    return super.toString() + "[" + getState() + "]";
  }
}
