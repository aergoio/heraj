package hera.build;

public interface ResourceFinder<D extends ResourceDependency, R extends Resource> {

  R find(D dependency);
}
