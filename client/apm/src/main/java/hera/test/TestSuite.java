/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class TestSuite {
  @Getter
  @Setter
  protected String name;

  @Getter
  @Setter
  protected List<TestCase> testCases = new ArrayList<>();

  public void addTestCase(final TestCase testCase) {
    this.testCases.add(testCase);
  }

  public long getTests() {
    return this.testCases.size();
  }

  public long getSuccesses() {
    return this.testCases.stream().filter(TestCase::isSuccess).count();
  }

  public long getFailures() {
    return this.testCases.stream().filter(testCase -> !testCase.isSuccess()).count();
  }
}
