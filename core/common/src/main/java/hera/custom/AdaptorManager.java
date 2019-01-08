/*
 * @copyright defined in LICENSE.txt
 */

package hera.custom;

import hera.Custom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import lombok.Getter;

public class AdaptorManager {

  @Getter
  protected static final AdaptorManager instance = new AdaptorManager();

  protected Set<Adaptee<?>> initialized = new HashSet<Adaptee<?>>();

  /**
   * Get and return registered adaptees for {@code candidateClass}.
   *
   * @param <AdapteeT> adaptee type
   * @param candidateClass adaptee type class
   *
   * @return adaptees
   */
  @SuppressWarnings("unchecked")
  public <AdapteeT> List<? extends AdapteeT> getAdaptors(Class<AdapteeT> candidateClass) {
    final ServiceLoader<Custom> serviceLoader = ServiceLoader.load(Custom.class);
    final List<AdapteeT> list = new ArrayList<AdapteeT>();
    for (final Custom custom : serviceLoader) {
      if (candidateClass.isInstance(custom)) {
        list.add((AdapteeT) custom);
      }
    }

    for (final AdapteeT adaptee : list) {
      if (adaptee instanceof Adaptee<?>) {
        final Adaptee<?> custom = ((Adaptee<?>) adaptee);
        if (!initialized.contains(custom)) {
          custom.initialize(this);
          initialized.add(custom);
        }
      }
    }

    return list;
  }
}
