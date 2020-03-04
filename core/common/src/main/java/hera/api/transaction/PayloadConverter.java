/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.Payload;

@ApiAudience.Public
@ApiStability.Unstable
public interface PayloadConverter<T extends Payload> {

  BytesValue convertToPayload(T t);

  T parseToModel(BytesValue payload);

}
