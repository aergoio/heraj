package hera.build.dep;

import hera.build.Resource;
import hera.build.ResourceDependency;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SourceImport implements ResourceDependency {
  @Getter
  protected final Resource source;

  @Getter
  protected final String path;

  @Override
  public String toString() {
    return "Import[" + path + "] from " + source;
  }
}
