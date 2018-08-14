/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.res;

import hera.ProjectFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Project {

  @Getter
  @NonNull
  protected final String location;

  @Getter
  @NonNull
  protected final ProjectFile projectFile;

  public Path getPath() {
    return Paths.get(location);
  }

  @Override
  public int hashCode() {
    return location.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof Project)) {
      return false;
    }
    final Project that = (Project) obj;

    return location.equals(that.location);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + location + "]";
  }
}
