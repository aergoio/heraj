/*
 * @copyright defined in LICENSE.txt
 */

package hera.example;

public abstract class AbstractExample {

  protected String hostname = "localhost:7845";

  protected void sleep(final long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public abstract void run() throws Exception;

}
