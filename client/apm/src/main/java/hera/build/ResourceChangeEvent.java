/*
 * @copyright defined in LICENSE.txt
 */

package hera.build;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResourceChangeEvent {
  @Getter
  protected final Resource resource;
}
