package hera.build.web.model;

import lombok.Getter;
import lombok.Setter;

public class QueryResult {

  @Getter
  @Setter
  protected Object result;

  public QueryResult() {
  }

  public QueryResult(final Object result) {
    this.result = result;
  }
}
