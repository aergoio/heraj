package hera.build.dep;

import hera.build.ResourceDependency;
import hera.build.res.Project;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuildEntry implements ResourceDependency {
  @Getter
  protected final Project project;

  @Getter
  protected final String target;
}
