package hera.build.web.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class BuildDependency {
  @Getter
  @Setter
  protected String name;

  @Getter
  @Setter
  protected List<BuildDependency> children = new ArrayList<>();

  public void add(BuildDependency child) {
    this.children.add(child);
  }
}
