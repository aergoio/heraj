/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.Stake;
import java.util.Collections;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class StakePayloadConverter implements PayloadConverter<Stake> {

  protected final Logger logger = getLogger(getClass());

  @Override
  public BytesValue convertToPayload(final Stake stake) {
    logger.debug("Convert to payload from {}", stake);
    final String json = JsonResolver.asJsonForm(stake.getOperationName(), Collections.emptyList());
    return BytesValue.of(json.getBytes());
  }

  @Override
  public Stake parseToModel(final BytesValue payload) {
    throw new UnsupportedOperationException();
  }

}
