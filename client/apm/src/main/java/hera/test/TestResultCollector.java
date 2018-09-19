/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import static hera.test.TestReportNodeResult.Failure;
import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertNull;
import static hera.util.ValidationUtils.assertTrue;
import static java.lang.System.currentTimeMillis;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;

@ToString(of = "testFiles")
public class TestResultCollector {

  protected final transient Logger logger = getLogger(getClass());

  @Getter
  protected TestReportNode currentTestFile;

  @Getter
  protected TestReportNode currentTestSuite;

  @Getter
  protected TestReportNode currentTestCase;

  protected Map<String, TestReportNode> testFiles = new LinkedHashMap<>();

  /**
   * Clear state fields.
   */
  public void clear() {
    this.currentTestFile = null;
    this.currentTestSuite = null;
    this.currentTestCase = null;
  }

  /**
   * Start to run test file.
   *
   * @param filename file name
   */
  public void start(final String filename) {
    assertNull(this.currentTestFile);
    assertNull(this.currentTestSuite);
    assertNull(this.currentTestCase);
    currentTestFile = new TestReportNode();
    currentTestFile.setName(filename);
    testFiles.put(filename, currentTestFile);
  }

  /**
   * End to run test file.
   */
  public void end() {
    this.currentTestCase = null;
    this.currentTestSuite = null;
    this.currentTestFile = null;
  }

  /**
   * Start test suite.
   *
   * @param suiteName test suite name
   */
  public void startSuite(final String suiteName) {
    assertNull(this.currentTestSuite);
    assertNotNull(suiteName);
    currentTestSuite = new TestReportNode();
    currentTestSuite.setName(suiteName);
    this.currentTestFile.addChild(currentTestSuite);
  }

  /**
   * End test suite.
   *
   * @param suiteName test suite name
   */
  public void endSuite(final String suiteName) {
    assertNotNull(this.currentTestSuite);
    assertTrue(this.currentTestSuite.getName().equals(suiteName));
    this.currentTestSuite = null;
  }

  /**
   * Start test case.
   *
   * @param testCaseName test case name
   */
  public void startCase(final String testCaseName) {
    assertNull(this.currentTestCase);
    assertNotNull(testCaseName);
    currentTestCase = new TestReportNode();
    currentTestCase.setName(testCaseName);
    this.currentTestSuite.addChild(currentTestCase);
  }

  /**
   * Catch error from test case.
   *
   * @param testCaseName test case name
   *
   * @param error error message
   */
  public void error(final String testCaseName, final String error) {
    assertNotNull(currentTestCase);
    assertTrue(currentTestCase.getName().equals(testCaseName));
    currentTestCase.setResult(Failure);
    currentTestCase.setResultDetail(error);
  }

  /**
   * End test case.
   *
   * @param testCaseName test case name
   */
  public void endCase(final String testCaseName) {
    assertNotNull(currentTestCase);
    assertTrue(currentTestCase.getName().equals(testCaseName));
    currentTestCase.setEndTime(currentTimeMillis());
    this.currentTestCase = null;
  }

  /**
   * Result test suites.
   *
   * @return test suites
   */
  public Collection<TestReportNode> getResults() {
    return testFiles.values();
  }

}
