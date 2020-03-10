/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.Vote;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class VotePayloadConverter implements PayloadConverter<Vote> {

  protected final Logger logger = getLogger(getClass());

  protected final JsonMapper mapper = new AergoJsonMapper();

  @Override
  public BytesValue convertToPayload(final Vote vote) {
    logger.debug("Convert to payload from {}", vote);
    final Map<String, Object> map = new HashMap<>();
    map.put("Name", vote.getOperationName());
    map.put("Args", vote.getCandidates());
    return mapper.marshal(map);
  }

  @Override
  public Vote parseToModel(final BytesValue payload) {
    throw new UnsupportedOperationException();
  }

}
