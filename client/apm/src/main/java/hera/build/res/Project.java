package hera.build.res;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

import hera.ProjectFile;
import hera.build.Resource;
import hera.build.ResourceDependency;
import hera.build.dep.BuildEntry;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString(of = "location")
public class Project implements Resource {

  @Getter
  protected final String location;

  @Getter
  protected final ProjectFile projectFile;

  @Override
  public Path getPath() {
    return Paths.get(location);
  }

  @Override
  public List<ResourceDependency> getDependencies() {
    return asList(new BuildEntry(this, projectFile.getSource()));
  }

  @Override
  public <T> Optional<T> adapt(Class<T> adaptor) {
    if (adaptor.isInstance(projectFile)) {
      return (Optional<T>) ofNullable(projectFile);
    }
    return Optional.empty();
  }
}
