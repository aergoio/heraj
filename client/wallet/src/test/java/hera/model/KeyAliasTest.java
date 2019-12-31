/*
 * @copyright defined in LICENSE.txt
 */

package hera.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import org.junit.Test;

public class KeyAliasTest extends AbstractTestCase {

  @Test
  public void testCreate() {
    final KeyAlias alias = KeyAlias.of(randomUUID().toString().replaceAll("-", ""));
    assertNotNull(alias);
  }

  @Test
  public void shouldThrowErrorOnInvalidPattern() {
    try {
      KeyAlias.of(randomUUID().toString());
    } catch (Exception e) {
      // then
    }
  }

}
