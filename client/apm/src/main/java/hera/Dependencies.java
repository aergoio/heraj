/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.Optional.ofNullable;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Dependencies {
  protected HashSet<Project> nodes = new HashSet<>();

  protected ListMultimap<Project, Project> from2to = LinkedListMultimap.create();
  protected ListMultimap<Project, Project> to2from = LinkedListMultimap.create();

  public void add(final Project node) {
    this.nodes.add(node);
  }

  public boolean contains(final Project node) {
    return this.nodes.contains(node);
  }

  public boolean contains(final String name) {
    return nodes.stream().map(Project::getName).anyMatch(name::equals);
  }

  public void addDependency(final Project source, final Project target) {
    from2to.put(source, target);
    to2from.put(target, source);
  }

  /**
   * Sort topologically for build sequence.
   *
   * @return build sequence
   */
  public List<Project> sort() {
    final ListMultimap<Project, Project> from2to = LinkedListMultimap.create();
    final ListMultimap<Project, Project> to2from = LinkedListMultimap.create();
    this.from2to.keys().forEach(key -> from2to.putAll(key, this.from2to.get(key)));
    this.to2from.keys().forEach(key -> to2from.putAll(key, this.to2from.get(key)));

    final Set<Project> incommingNodes = from2to.values().stream().collect(Collectors.toSet());
    final List<Project> startingNodes =
        nodes.stream().filter(node -> !incommingNodes.contains(node)).collect(Collectors.toList());

    final List<Project> sortedList = new ArrayList<>();

    while (!startingNodes.isEmpty()) {
      final Project from = startingNodes.remove(0);
      sortedList.add(from);

      from2to.get(from).forEach(to -> {
        to2from.remove(to, from);
        if (ofNullable(to2from.get(to)).map(List::isEmpty).orElse(true)) {
          startingNodes.add(to);
        }
      });
      from2to.removeAll(from);
    }

    if (to2from.isEmpty()) {
      return sortedList;
    } else {
      return null;
    }
  }
}
