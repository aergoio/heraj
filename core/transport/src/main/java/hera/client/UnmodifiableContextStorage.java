/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.util.ValidationUtils.assertNotNull;

import hera.Context;
import hera.ContextStorage;
import lombok.Getter;

class UnmodifiableContextStorage implements ContextStorage<Context> {

  @Getter
  protected final Context context;

  UnmodifiableContextStorage(final Context context) {
    assertNotNull(context, "Context must not null");
    this.context = context;
  }

  @Override
  public Context get() {
    return this.context;
  }

  @Override
  public Context put(final Context context) {
    throw new UnsupportedOperationException("Cannot put context");
  }

}
