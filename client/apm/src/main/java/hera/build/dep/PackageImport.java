package hera.build.dep;

import hera.build.ResourceDependency;
import hera.build.res.Source;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class PackageImport implements ResourceDependency {
  @Getter
  protected final Source source;

  @Getter
  protected final String packageName;

}
