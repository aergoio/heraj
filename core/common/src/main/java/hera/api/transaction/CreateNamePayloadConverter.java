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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;


@ApiAudience.Public
@ApiStability.Unstable
public class CreateNamePayloadConverter implements PayloadConverter<CreateName> {

  protected final Logger logger = getLogger(getClass());

  protected final JsonMapper mapper = new AergoJsonMapper();

  @Override
  public BytesValue convertToPayload(final CreateName createName) {
    logger.debug("Convert to payload from {}", createName);
    final List<Object> args = new ArrayList<>();
    args.add(createName.getName().getValue());
    final Map<String, Object> map = new HashMap<>();
    map.put("Name", createName.getOperationName());
    map.put("Args", args);
    return mapper.marshal(map);
  }

  @Override
  public CreateName parseToModel(final BytesValue payload) {
    throw new UnsupportedOperationException();
  }

}
