/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertNull;
import static hera.util.ValidationUtils.assertTrue;
import static java.lang.System.currentTimeMillis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;

@ToString(of = "testFiles")
public class TestResultCollector {

  @Getter
  protected TestFile currentTestFile;

  @Getter
  protected TestSuite currentTestSuite;

  @Getter
  protected TestCase currentTestCase;

  protected Map<String, TestFile> testFiles = new LinkedHashMap<>();

  public void clear() {
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
    currentTestFile = new TestFile();
    currentTestFile.setFilename(filename);
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
    currentTestSuite = new TestSuite();
    currentTestSuite.setName(suiteName);
    this.currentTestFile.addSuite(currentTestSuite);
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
    currentTestCase = new TestCase(testCaseName);
    this.currentTestSuite.addTestCase(currentTestCase);
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
    currentTestCase.setErrorMessage(error);
    if (currentTestSuite.isSuccess()) {
      currentTestSuite.setError(error);
    }
    currentTestFile.setSuccess(false);
  }

  /**
   * End test case.
   *
   * @param testCaseName test case name
   *
   * @param success test case result
   */
  public void endCase(final String testCaseName, final boolean success) {
    assertNotNull(currentTestCase);
    assertTrue(currentTestCase.getName().equals(testCaseName));
    currentTestCase.setSuccess(success);
    currentTestCase.setEndTime(currentTimeMillis());
    this.currentTestCase = null;
  }

  /**
   * Result test suites.
   *
   * @return test suites
   */
  public Collection<TestFile> getResults() {
    return testFiles.values();
  }

}
