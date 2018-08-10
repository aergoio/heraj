package hera.util;

import java.util.Map.Entry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class SimpleEdge<NodeT> implements Edge<NodeT> {

  @Getter
  protected final NodeT source;

  @Getter
  protected final NodeT destination;

  public SimpleEdge(final Entry<NodeT, NodeT> e) {
    this(e.getKey(), e.getValue());
  }

  public Edge<NodeT> inverse() {
    return new SimpleEdge<>(this.destination, this.source);
  }

}
