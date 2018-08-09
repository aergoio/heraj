package hera.util;

import java.util.List;
import org.junit.Test;

public class GraphTest extends AbstractTestCase {

  @Test
  public void testSort() {
    final Graph<String, Edge<String>> graph = new Graph<>();
    graph.add("n1");
    graph.add("n2");

    graph.add(new SimpleEdge<>("n1", "n2"));

    final List<String> sorted = graph.sort();
    logger.debug("Sorted: {}", sorted);
  }

  @Test
  public void shouldSortDiamondTopology() {
    final Graph<String, Edge<String>> graph = new Graph<>();

    graph.add("n1");
    graph.add("n2");
    graph.add("n3");
    graph.add("n4");

    graph.add(new SimpleEdge<>("n1", "n2"));
    graph.add(new SimpleEdge<>("n1", "n3"));
    graph.add(new SimpleEdge<>("n2", "n4"));
    graph.add(new SimpleEdge<>("n3", "n4"));

    final List<String> sorted = graph.sort();
    logger.debug("Sorted: {}", sorted);
  }

}