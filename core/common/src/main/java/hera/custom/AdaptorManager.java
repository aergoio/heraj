/*
 * @copyright defined in LICENSE.txt
 */

package hera.custom;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import hera.Custom;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import lombok.Getter;

public class AdaptorManager {

  @Getter
  protected static final AdaptorManager instance = new AdaptorManager();

  protected Set<Adaptee<?>> initialized = new HashSet<>();

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
    final List<? extends AdapteeT> list =
        (List<? extends AdapteeT>) stream(serviceLoader.spliterator(), false)
        .filter(candidateClass::isInstance)
        .collect(toList());

    list.stream()
        .filter(custom -> custom instanceof Adaptee<?>)
        .map(Adaptee.class::cast)
        .filter(custom -> !initialized.contains(custom))
        .forEach(adaptee -> {
          adaptee.initialize(this);
          initialized.add(adaptee);
        });
    return list;
  }
}
