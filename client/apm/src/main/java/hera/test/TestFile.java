package hera.test;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class TestFile {
  @Getter
  @Setter
  protected String filename;

  @Getter
  @Setter
  protected boolean success = true;

  @Getter
  @Setter
  protected LuaErrorInformation error;

  @Getter
  @Setter
  protected List<TestSuite> suites = new ArrayList<>();

  public long getTests() {
    return this.suites.size();
  }

  public long getSuccesses() {
    return this.suites.stream().filter(TestSuite::isSuccess).count();
  }

  public long getFailures() {
    return this.suites.stream().filter(testSuite -> !testSuite.isSuccess()).count();
  }

  public void addSuite(final TestSuite suite) {
    this.suites.add(suite);
  }
}
