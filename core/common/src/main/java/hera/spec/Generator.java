package hera.spec;

public interface Generator<T> {

  /**
   * Generate type T satisfying spec for it using a provided arguments.
   *
   * @param objects objects to generate type T
   * @return a spec satisfying type T
   */
  T generate(Object... objects);

}
