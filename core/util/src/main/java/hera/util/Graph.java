package hera.util;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.ToString;
import org.slf4j.Logger;

@ToString(of = "from2to")
public class Graph<NodeT, EdgeT extends Edge<NodeT>> {

  protected final transient Logger logger = getLogger(getClass());

  protected final Set<NodeT> nodes;

  protected final SetMultimap<NodeT, NodeT> from2to;
  protected final SetMultimap<NodeT, NodeT> to2from;

  protected static <NodeT> SetMultimap<NodeT, NodeT> inverseEdge(
      final SetMultimap<NodeT, NodeT> from2to) {
    final SetMultimap<NodeT, NodeT> inversed = HashMultimap.create();
    from2to.entries().forEach(e -> inversed.put(e.getValue(), e.getKey()));
    return inversed;
  }

  /**
   * Default constructor.
   */
  public Graph() {
    this(new HashSet<>(), HashMultimap.create());
  }

  /**
   * Constructor with node and edge information.
   *
   * @param nodes   node information
   * @param from2to edge information
   */
  public Graph(final Set<NodeT> nodes, final SetMultimap<NodeT, NodeT> from2to) {
    this.nodes = new HashSet<>(nodes);
    this.from2to = HashMultimap.create(from2to);
    this.to2from = HashMultimap.create(inverseEdge(from2to));
  }

  /**
   * Add node.
   *
   * <p>
   *   This method return this instance because it is builder method
   * </p>
   *
   * @param node node to add
   *
   * @return this
   */
  public Graph<NodeT, EdgeT> add(final NodeT node) {
    this.nodes.add(node);
    return this;
  }

  /**
   * Add edge.
   *
   * <p>
   *   This method return this instance because it is builder method
   * </p>
   *
   * @param edge edge to add
   *
   * @return this
   */
  public Graph<NodeT, EdgeT> add(final EdgeT edge) {
    logger.trace("Adding {}...", edge);
    assertNotNull(edge.getSource(), "The source of edge must not be null");
    assertNotNull(edge.getDestination(), "The destination of edge must not be null");
    from2to.put(edge.getSource(), edge.getDestination());
    to2from.put(edge.getDestination(), edge.getSource());
    return this;
  }

  public Graph<NodeT, EdgeT> inverse() {
    return new Graph<>(nodes, to2from);
  }

  /**
   * Build other graph without {@code nodesToRemove}.
   *
   * @param nodesToRemove nodes to remove
   *
   * @return new graph without specific nodes
   */
  public Graph<NodeT, EdgeT> remove(final Set<NodeT> nodesToRemove) {
    final Set<NodeT> subNodes = nodes.stream()
        .filter(n -> !nodesToRemove.contains(n)).collect(toSet());

    final SetMultimap<NodeT, NodeT> subEdges = HashMultimap.create();
    from2to.entries().stream()
        .filter(e -> !nodesToRemove.contains(e.getKey()) && !nodesToRemove.contains(e.getValue()))
        .forEach(e -> subEdges.put(e.getKey(), e.getValue()));

    return new Graph<>(subNodes, subEdges);
  }

  /**
   * Sort topologically.
   *
   * @return node sequence to be sorted
   */
  public List<NodeT> sort() {
    // State to be updated
    final Set<NodeT> startings = getStartingPoints();
    logger.debug("Startings: {}", startings);
    if (nodes.isEmpty()) {
      return Collections.emptyList();
    } else if (startings.isEmpty()) {
      return null;
    }

    final Graph<NodeT, EdgeT> subGraph = this.remove(startings);
    logger.debug("Subgraph: {}", subGraph);

    final List<NodeT> remains = subGraph.sort();
    if (null == remains) {
      return null;
    }

    final List<NodeT> sorted = new ArrayList<>();
    sorted.addAll(startings);
    sorted.addAll(remains);
    return sorted;
  }

  /**
   * Return if graph has cycle.
   *
   * @return if graph hash cycle
   */
  public boolean hasCycle() {
    return null == sort();
  }

  /**
   * Return starting nodes.
   * <p>
   *   The starting node is node without incoming edge
   * </p>
   *
   * @return starting point
   */
  public Set<NodeT> getStartingPoints() {
    final Set<NodeT> keys = to2from.keySet().stream()
        .filter(node -> !to2from.get(node).isEmpty()).collect(toSet());
    return nodes.stream().filter(node -> !keys.contains(node)).collect(toSet());
  }
}
