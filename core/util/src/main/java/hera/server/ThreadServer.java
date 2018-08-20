/*
 * @copyright defined in LICENSE.txt
 */

package hera.server;

import static hera.server.ServerStatus.BOOTING;
import static hera.server.ServerStatus.DOWNING;
import static hera.server.ServerStatus.INITIALIZING;
import static hera.server.ServerStatus.PROCESSING;
import static hera.server.ServerStatus.SKIP;
import static hera.server.ServerStatus.TERMINATED;
import static hera.server.ServerStatus.TERMINATING;
import static hera.server.StateConditionFactory.not;
import static hera.server.StateConditionFactory.when;

import hera.util.StringUtils;

public class ThreadServer extends AbstractServer implements Runnable {

  /**
   * Thread for current server.
   */
  protected Thread thread = null;

  /**
   * Template method for execution.
   *
   * @see #initialize()
   * @see #process()
   * @see #handleError(Throwable)
   * @see #terminate()
   */
  @Override
  public void run() {
    logger.trace("Starting {} server...", getName());

    // Prevent halt.
    state.getLock().writeLock().lock();
    try {
      if (isStatus(DOWNING)) {
        logger.info("Starting is cancelled.");
        changeStatus(TERMINATED);
        return;
      }
      logger.debug("Doing pre-process...");
      changeStatus(INITIALIZING);
    } catch (final Throwable e) {
      logger.error("Server got unexpected exception:", e);
    } finally {
      state.getLock().writeLock().unlock();
    }

    clearException();
    try {
      initialize();
      logger.info("Pre-process done.");
    } catch (final Throwable e) {
      this.exception = e;
      logger.error("Exception occurred in pre-process.", e);
      changeStatus(TERMINATED);
      return;
    }

    state.getLock().writeLock().lock();
    try {
      if (isStatus(DOWNING)) {
        changeStatus(TERMINATING);
      } else {
        changeStatus(PROCESSING);
        logger.info("{} started.", this);
      }
    } finally {
      state.getLock().writeLock().unlock();
    }

    try {
      logger.debug("{}'s main process started.", this);
      loop();
      logger.debug("{}'s main process done.", this);
    } finally {
      logger.debug("Doing post-process...");
      try {
        terminate();
        logger.info("Post-process done.");

      } catch (final Throwable th) {
        logger.error("Exception occurred in post-process.", th);
      } finally {
        changeStatus(TERMINATED);
      }

    }
  }

  protected final ServerStatus[] loopCondition = new ServerStatus[]{PROCESSING, SKIP};
  protected final ServerStatus[] processingCondition = new ServerStatus[]{PROCESSING};

  /**
   * Loop for task.
   */
  protected void loop() {
    while (isStatus(loopCondition)) {
      if (isStatus(processingCondition)) {
        try {
          logger.trace("{}'s task started.", this);
          process();
          logger.trace("{}'s task done.", this);
        } catch (final Throwable e) {
          exception = e;

          logger.error("Error :{}", StringUtils.nvl(e.getLocalizedMessage(), e.getMessage()), e);

          handleError(e);
        }
      }

    }
  }

  /**
   * Method for task.
   * <p>
   * Must be implemented under performance consideration.
   * </p>
   *
   * @throws Exception If exception in process
   */
  protected void process() throws Exception {
    // Not implemented
  }

  /* (non-Javadoc)
   * @see escode.server.Server#boot()
   */
  @Override
  public void boot() {
    this.boot(false);
  }

  /**
   * Start server.
   * <p>
   * Wait for server to be start if {@code bBlock} is {@code true}
   * </p>
   * @param isBlock wait flag
   */
  public void boot(final boolean isBlock) {
    if (!state.changeState(BOOTING, when(TERMINATED))) {
      logger.error("{} is not terminated status. status: {}", this, state);

      throw new IllegalStateException();
    }

    thread = new Thread(this, getName());
    logger.debug("Starting {}...", this);
    thread.start();

    logger.trace("Staring post-process for boot.");
    postBoot();
    if (isBlock) {
      waitStatus(PROCESSING, TERMINATED);
    }
  }

  protected void postBoot() {
  }

  @Override
  public void down() {
    down(false);
  }

  /**
   * Down server.
   *
   * @param isBlock flag if wait to be done
   */
  public void down(final boolean isBlock) {
    if (!state.changeState(DOWNING, when(not(DOWNING, TERMINATING, TERMINATED)))) {
      logger.error("{} is already stopped.", this);
      return;
    }

    logger.trace("Staring pre-process for down...");
    preDown();

    this.thread = null;
    if (isBlock) {
      logger.debug("Wait for {} to be stopped.", this);
      waitStatus(ServerStatus.TERMINATED);
    }
    logger.info("{} stopped.", this);
  }

  /**
   * Pre-process for down.
   */
  protected void preDown() {
  }

  @Override
  public String toString() {
    return "Server[" + getName() + "]";
  }
}
