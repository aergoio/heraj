/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class ModuleStatus {

  @Getter
  @Setter
  protected String moduleName;

  @Getter
  @Setter
  protected String status;

  @Getter
  @Setter
  protected long processedMessageCount;

  @Getter
  @Setter
  protected long queuedMessageCount;

  @Getter
  @Setter
  protected double latencyInMicroseconds;

  @Getter
  @Setter
  protected String error;

}
