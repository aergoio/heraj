/*
 * @copyright defined in LICENSE.txt
 */

package hera.example;

public abstract class AbstractExample {

  protected final String hostname = "localhost:7845";

  protected void sleep(final long miliseconds) {
    try {
      Thread.sleep(miliseconds);
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }
  }

  public abstract void run();

}
