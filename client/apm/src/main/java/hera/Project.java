/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
public class Project {
  @Getter
  @Setter
  protected String name;

  @Getter
  @Setter
  protected String branch;

  @Getter
  @Setter
  protected List<Project> dependencies;

  @Getter
  @Setter
  protected List<String> sources;
}
