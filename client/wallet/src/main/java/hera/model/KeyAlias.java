/*
 * @copyright defined in LICENSE.txt
 */

package hera.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Identity;
import hera.exception.HerajException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@ApiAudience.Public
@ApiStability.Unstable
@EqualsAndHashCode
public class KeyAlias implements Identity {

  protected static final Pattern ALIAS_PATTERN;

  static {
    ALIAS_PATTERN = Pattern.compile("[a-zA-Z0-9]+");
  }

  public static KeyAlias of(final String value) {
    return new KeyAlias(value);
  }

  @Getter
  protected final String value;

  /**
   * KeyAlias constructor.
   *
   * @param value an alias value. Must be "[a-zA-Z0-9]+"
   * @throws HerajException if alias format is wrong
   */
  public KeyAlias(final String value) {
    assertNotNull(value);
    final Matcher matcher = ALIAS_PATTERN.matcher(value);
    if (!matcher.matches()) {
      throw new HerajException("Key alias must be " + ALIAS_PATTERN.toString());
    }
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }

}
