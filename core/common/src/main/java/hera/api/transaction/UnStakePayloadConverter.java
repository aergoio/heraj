/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.UnStake;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class UnStakePayloadConverter implements PayloadConverter<UnStake> {

  protected final Logger logger = getLogger(getClass());

  protected final JsonMapper mapper = new AergoJsonMapper();

  @Override
  public BytesValue convertToPayload(final UnStake stake) {
    logger.debug("Convert to payload from {}", stake);
    final Map<String, Object> map = new HashMap<>();
    map.put("Name", stake.getOperationName());
    map.put("Args", Collections.emptyList());
    return mapper.marshal(map);
  }

  @Override
  public UnStake parseToModel(final BytesValue payload) {
    throw new UnsupportedOperationException();
  }

}
