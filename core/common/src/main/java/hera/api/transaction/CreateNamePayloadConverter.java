/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.CreateName;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;


@ApiAudience.Public
@ApiStability.Unstable
public class CreateNamePayloadConverter implements PayloadConverter<CreateName> {

  protected final Logger logger = getLogger(getClass());

  @Override
  public BytesValue convertToPayload(final CreateName createName) {
    logger.debug("Convert to payload from {}", createName);
    final List<Object> args = new ArrayList<>();
    args.add(createName.getName().getValue());
    final String json = JsonResolver.asJsonForm(createName.getOperationName(), args);
    return BytesValue.of(json.getBytes());
  }

  @Override
  public CreateName parseToModel(final BytesValue payload) {
    throw new UnsupportedOperationException();
  }

}
