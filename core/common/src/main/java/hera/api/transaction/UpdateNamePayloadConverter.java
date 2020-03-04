/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.UpdateName;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class UpdateNamePayloadConverter implements PayloadConverter<UpdateName> {

  protected final Logger logger = getLogger(getClass());

  @Override
  public BytesValue convertToPayload(final UpdateName updateName) {
    logger.debug("Convert to payload from {}", updateName);
    final List<Object> args = new ArrayList<>();
    args.add(updateName.getName().getValue());
    args.add(updateName.getNextOwner().getValue());
    final String json = JsonResolver.asJsonForm(updateName.getOperationName(), args);
    return BytesValue.of(json.getBytes());
  }

  @Override
  public UpdateName parseToModel(final BytesValue payload) {
    throw new UnsupportedOperationException();
  }

}
