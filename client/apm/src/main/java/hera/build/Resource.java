package hera.build;

import hera.util.Node;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface Resource extends Node {
  Path getPath();

  List<ResourceDependency> getDependencies() throws Exception;

  <T> Optional<T> adapt(Class<T> adaptor);
}
