/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertNull;
import static hera.util.ValidationUtils.assertTrue;
import static java.lang.System.currentTimeMillis;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TestReporter {

  protected TestSuite currentTestSuite;

  protected TestCase currentTestCase;

  protected Map<String, TestSuite> testSuites = new LinkedHashMap<>();

  public void startSuite(final String suiteName) {
    assertNull(this.currentTestSuite);
    assertNotNull(suiteName);
    currentTestSuite = new TestSuite();
    currentTestSuite.setName(suiteName);
    testSuites.put(suiteName, currentTestSuite);
  }

  public void endSuite(final String suiteName) {
    assertNotNull(this.currentTestSuite);
    assertTrue(this.currentTestSuite.getName().equals(suiteName));
    this.currentTestSuite = null;
  }

  public void start(final String testCaseName) {
    assertNull(this.currentTestCase);
    assertNotNull(testCaseName);
    currentTestCase = new TestCase(testCaseName);
    this.currentTestSuite.addTestCase(currentTestCase);
  }

  public void error(final String testCaseName, final String error) {
    assertNotNull(currentTestCase);
    assertTrue(currentTestCase.getName().equals(testCaseName));
    currentTestCase.setErrorMessage(error);
  }

  public void end(final String testCaseName, final boolean success) {
    assertNotNull(currentTestCase);
    assertTrue(currentTestCase.getName().equals(testCaseName));
    currentTestCase.setSuccess(success);
    currentTestCase.setEndTime(currentTimeMillis());
    this.currentTestCase = null;
  }

  public Collection<TestSuite> getResults() {
    return testSuites.values();
  }

}
