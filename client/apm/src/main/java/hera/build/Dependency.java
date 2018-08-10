package hera.build;

import hera.util.SimpleEdge;
import lombok.ToString;

@ToString(callSuper = true)
public class Dependency extends SimpleEdge<Resource> {

  protected final String type;

  public Dependency(final String type, final Resource source, final Resource destination) {
    super(source, destination);
    this.type = type;
  }
}
