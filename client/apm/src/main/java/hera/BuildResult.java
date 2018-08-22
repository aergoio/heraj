/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.UUID.randomUUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class BuildResult {

  @Getter
  @Setter
  protected String uuid = randomUUID().toString();

  @Getter
  @Setter
  protected long timestamp = System.currentTimeMillis();

  @Getter
  @Setter
  protected boolean success = true;

  @Getter
  @Setter
  protected FileSet fileSet = new FileSet();

}
