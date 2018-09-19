package hera.test;

import static hera.test.TestReportNodeResult.Failure;
import static hera.test.TestReportNodeResult.Success;
import static hera.test.TestReportNodeResult.Unknown;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
public class TestReportNode<ResultDetailT> {

  @Getter
  @Setter
  protected String name;

  @Getter
  @Setter
  protected TestReportNodeResult result = Unknown;

  @Getter
  @Setter
  protected long startTime = currentTimeMillis();

  @Getter
  @Setter
  protected long endTime;

  @Getter
  @Setter
  protected ResultDetailT resultDetail;

  @Getter
  @Setter
  @NonNull
  protected List<TestReportNode<?>> children = new ArrayList<>();

  /**
   * Constructor with name, result, resultDetail and children.
   *
   * @param name node name
   * @param result test result
   * @param resultDetail result details
   * @param children child nodes
   */
  public TestReportNode(
      final String name,
      final TestReportNodeResult result,
      final ResultDetailT resultDetail,
      final TestReportNode<?>... children) {
    this.name = name;
    this.result = result;
    this.resultDetail = resultDetail;
    this.children.addAll(asList(children));
  }

  /**
   * Return if this node is success.
   *
   * @return if success
   */
  public boolean isSuccess() {
    switch (this.result) {
      case Success:
        return true;
      case Failure:
        return false;
      default:
        return children.stream().allMatch(child -> child.isSuccess());
    }
  }

  /**
   * Return if this node is failure.
   *
   * @return if failure
   */
  public boolean isFailure() {
    switch (this.result) {
      case Success:
        return false;
      case Failure:
        return true;
      default:
        return children.stream().anyMatch(child -> child.isFailure());
    }
  }

  /**
   * Return how many tests this node has.
   *
   * @return the number of tests
   */
  public int getTheNumberOfTests() {
    if (children.isEmpty()) {
      return 1;
    } else {
      return children.stream().map(TestReportNode::getTheNumberOfTests)
          .reduce((a, b) -> a + b).orElse(0);
    }
  }

  /**
   * Return how many successes this node has.
   *
   * @return the number of successes
   */
  public int getTheNumberOfSuccesses() {
    if (children.isEmpty()) {
      if (this.resultDetail == Success) {
        return 1;
      } else {
        return 0;
      }
    } else {
      return children.stream().map(TestReportNode::getTheNumberOfSuccesses)
          .reduce((a, b) -> a + b).orElse(0);
    }
  }

  /**
   * Return how many failures this node has.
   *
   * @return the number of failures
   */
  public int getTheNumberOfFailures() {
    if (children.isEmpty()) {
      if (this.resultDetail == Failure) {
        return 1;
      } else {
        return 0;
      }
    } else {
      return children.stream().map(TestReportNode::getTheNumberOfFailures)
          .reduce((a, b) -> a + b).orElse(0);
    }
  }

  /**
   * Add child node.
   *
   * @param child node to add
   */
  public void addChild(TestReportNode<?> child) {
    this.children.add(child);
  }
}
