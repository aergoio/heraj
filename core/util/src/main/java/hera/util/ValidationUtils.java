/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.ObjectUtils.equal;

import java.util.function.Supplier;

public class ValidationUtils {
  /**
   * assert that {@code value} is {@code true}.
   * <p>
   *   throw {@link RuntimeException} for {@code supplier} to create if not {@code true}.
   * </p>
   * @param value    value to check
   * @param supplier supplier to create exception
   */
  public static void assertTrue(final boolean value, final Supplier<RuntimeException> supplier) {
    if (!value) {
      throw supplier.get();
    }
  }

  /**
   * assert that {@code value} is {@code true}.
   * <p>
   *   throw {@code th} if not {@code true}.
   * </p>
   * @param value value to check
   * @param th    exception to throw
   */
  public static void assertTrue(final boolean value, final RuntimeException th) {
    assertTrue(value, () -> th);
  }

  /**
   * assert that {@code value} is {@code true}.
   * <p>
   *   throw {@link AssertionError} containing @{code message} if not {@code true}.
   * </p>
   * @param value   value to check
   * @param message message which exception contains
   */
  public static void assertTrue(final boolean value, final String message) {
    if (!value) {
      throw new AssertionError(message);
    }
  }

  /**
   * assert that {@code value} is {@code true}.
   * <p>
   *   throw {@link AssertionError} if not {@code true}.
   * </p>
   * @param value value to check
   */
  public static void assertTrue(final boolean value) {
    assertTrue(value, "Expression must be true.");
  }

  /**
   * assert that {@code value} is {@code false}.
   * <p>
   *   throw {@link RuntimeException} for {@code supplier} to create if not {@code false}.
   * </p>
   *
   * @param value    value to check
   * @param supplier supplier to create exception
   */
  public static void assertFalse(final boolean value, final Supplier<RuntimeException> supplier) {
    if (value) {
      throw supplier.get();
    }
  }

  /**
   * assert that {@code value} is {@code false}.
   * <p>
   *   throw {@code th} if not {@code false}.
   * </p>
   *
   * @param value value to check
   * @param th    exception to throw
   */
  public static void assertFalse(final boolean value, final RuntimeException th) {
    assertFalse(value, () -> th);
  }

  /**
   * assert that {@code value} is {@code false}.
   * <p>
   *   throw {@link AssertionError} containing @{code message} if not {@code false}.
   * </p>
   * @param value   value to check
   * @param message message which exception contains
   */
  public static void assertFalse(final boolean value, final String message) {
    if (value) {
      throw new AssertionError(message);
    }
  }

  /**
   * assert that {@code value} is {@code false}.
   * <p>
   *   throw {@link AssertionError} if not {@code false}.
   * </p>
   * @param value value to check
   */
  public static void assertFalse(final boolean value) {
    assertFalse(value, "Expression must be false");
  }

  /**
   * assert that {@code obj} is null.
   * <p>
   *   throw {@link RuntimeException} for {@code supplier} to create if {@code obj} is not null
   * </p>
   *
   * @param obj object to check
   *
   * @param supplier supplier to create {@link RuntimeException}
   */
  public static void assertNull(final Object obj, final Supplier<RuntimeException> supplier) {
    assertTrue(null == obj, supplier);
  }

  /**
   * assert that {@code obj} is null.
   * <p>
   *   throw {@code th} if {@code obj} is not null
   * </p>
   * @param obj   object to check
   * @param error exception to throw
   */
  public static void assertNull(final Object obj, final RuntimeException error) {
    assertTrue(null == obj, error);
  }

  /**
   * assert that {@code obj} is null.
   * <p>
   *   throw {@link AssertionError} containing {@code message} if {@code obj} is not null.
   * </p>
   *
   * @param obj     object to check
   * @param message message which exception contains
   */
  public static void assertNull(final Object obj, final String message) {
    assertTrue(null == obj, message);
  }

  /**
   * assert that {@code obj} is null.
   * <p>
   *   throw {@link AssertionError} if {@code obj} is not null.
   * </p>
   * @param obj object to check
   */
  public static void assertNull(final Object obj) {
    assertNull(obj, "The object must be null");
  }

  /**
   * assert that {@code obj} is not null.
   * <p>
   *   throw {@link RuntimeException} for {@code supplier} to create if {@code obj} is null.
   * </p>
   *
   * @param obj object to check
   *
   * @param supplier supplier to create {@link RuntimeException}
   */
  public static void assertNotNull(final Object obj, final Supplier<RuntimeException> supplier) {
    assertTrue(null != obj, supplier);
  }

  /**
   * assert that {@code obj} is not null.
   * <p>
   *   throw {@code error} if {@code obj} is null.
   * </p>
   * @param obj   object to check
   * @param error exception to throw
   */
  public static void assertNotNull(final Object obj, final RuntimeException error) {
    assertTrue(null != obj, error);
  }

  /**
   * assert that {@code obj} is not null.
   * <p>
   *   throw {@link AssertionError} containing {@code message} if {@code obj} is null.
   * </p>
   *
   * @param obj     object to check
   * @param message message which exception contains
   */
  public static void assertNotNull(final Object obj, final String message) {
    assertTrue(null != obj, message);
  }

  /**
   * assert that {@code obj} is not null.
   * <p>
   *   throw {@link AssertionError} if {@code obj} is null.
   * </p>
   * @param obj object to check
   */
  public static void assertNotNull(final Object obj) {
    assertNotNull(obj, "The object must not be null");
  }

  /**
   * Assert that {@code obj1} is equal to {@code obj2}.
   * <p>
   *   throw {@link RuntimeException} for {@code supplier} to create if {@code obj1} is not equal
   *   to {@code obj2}.
   * </p>
   *
   * @param obj1      object1 to check
   * @param obj2      object2 to check
   * @param supplier  supplier to create {@link RuntimeException}
   */
  public static void assertEquals(
      final Object obj1,
      final Object obj2,
      final Supplier<RuntimeException> supplier) {

    assertTrue(equal(obj1, obj2), supplier);
  }

  /**
   * Assert that {@code obj1} is equal to {@code obj2}.
   * <p>
   *   throw {@code error} if {@code obj1} is not equal to {@code obj2}.
   * </p>
   *
   * @param obj1  object1 to check
   * @param obj2  object2 to check
   * @param error exception to throw
   */
  public static void assertEquals(
      final Object obj1,
      final Object obj2,
      final RuntimeException error) {

    assertTrue(equal(obj1, obj2), error);
  }

  /**
   * Assert that {@code obj1} is equal to {@code obj2}.
   * <p>
   *   throw {@link AssertionError} containing {@code message} if {@code obj1} is not equal to
   *   {@code obj2}.
   * </p>
   *
   * @param obj1    object1 to check
   * @param obj2    object2 to check
   * @param message message which exception contains
   */
  public static void assertEquals(
      final Object obj1,
      final Object obj2,
      final String message) {
    assertTrue(equal(obj1, obj2), message);
  }

  /**
   * Assert that {@code obj1} is equal to {@code obj2}.
   * <p>
   *   throw {@link AssertionError} if {@code obj1} is not equal to {@code obj2}.
   * </p>
   *
   * @param obj1    object1 to check
   * @param obj2    object2 to check
   */
  public static void assertEquals(
      final Object obj1,
      final Object obj2) {
    assertEquals(obj1, obj2, obj1 + " expected but " + obj2);
  }

  /**
   * Assert that {@code obj1} is not equal to {@code obj2}.
   * <p>
   *   throw {@link RuntimeException} for {@code supplier} to create if {@code obj1} is equal
   *   to {@code obj2}.
   * </p>
   *
   * @param obj1      object1 to check
   * @param obj2      object2 to check
   * @param supplier  supplier to create {@link RuntimeException}
   */
  public static void assertNotEquals(
      final Object obj1,
      final Object obj2,
      final Supplier<RuntimeException> supplier) {

    assertFalse(equal(obj1, obj2), supplier);
  }

  /**
   * Assert that {@code obj1} is not equal to {@code obj2}.
   * <p>
   *   throw {@code error} if {@code obj1} is equal to {@code obj2}.
   * </p>
   *
   * @param obj1  object1 to check
   * @param obj2  object2 to check
   * @param error exception to throw
   */
  public static void assertNotEquals(
      final Object obj1,
      final Object obj2,
      final RuntimeException error) {

    assertFalse(equal(obj1, obj2), error);
  }

  /**
   * Assert that {@code obj1} is not equal to {@code obj2}.
   * <p>
   *   throw {@link AssertionError} containing {@code message} if {@code obj1} is equal to
   *   {@code obj2}.
   * </p>
   *
   * @param obj1    object1 to check
   * @param obj2    object2 to check
   * @param message message which exception contains
   */
  public static void assertNotEquals(
      final Object obj1,
      final Object obj2,
      final String message) {

    assertFalse(equal(obj1, obj2), message);
  }

  /**
   * Assert that {@code obj1} is not equal to {@code obj2}.
   * <p>
   *   throw {@link AssertionError} if {@code obj1} is equal to {@code obj2}.
   * </p>
   *
   * @param obj1    object1 to check
   * @param obj2    object2 to check
   */
  public static void assertNotEquals(
      final Object obj1,
      final Object obj2) {
    assertNotEquals(obj1, obj2, "Two objects must be not equals");
  }
}
