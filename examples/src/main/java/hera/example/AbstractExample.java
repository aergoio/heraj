/*
 * @copyright defined in LICENSE.txt
 */

package hera.example;

import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;

public abstract class AbstractExample {

  protected String hostname = "localhost:7845";

  protected String richEncrypted =
      "47VYaB9WJ4FoBaiZ1HAh3yDBkSFDVM3fVxgjTfAnmmFa8GqyAZUb4gqKKirkoRgdhMazRHzbR";

  protected String richPassword = "password";

  protected AergoKey supplyKey() {
    return new AergoKeyGenerator().create(richEncrypted, richPassword);
  }

  public abstract void run() throws Exception;

}
