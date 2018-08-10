package hera.build;

import hera.build.res.Project;
import java.util.Optional;

public interface PackageRepository {
  Optional<Project> find(String name);
}
