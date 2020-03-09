/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BigNumber;
import hera.api.model.BytesValue;
import java.util.List;
import java.util.Map;

/**
 * Supported Object types.
 *
 * <pre>
 *   - {@link List} -&gt; json array
 *   - {@link Map} -&gt; json object
 *   - null -&gt; json null
 *   - {@link String} -&gt; json string
 *   - {@link Number} -&gt; json number
 *   - {@link Boolean} -&gt; json boolean
 *   - {@link BigNumber} -&gt; aergo specific bignumber object
 * </pre>
 *
 * @author taeiklim
 *
 */

@ApiAudience.Private
@ApiStability.Unstable
public interface JsonMapper {

  BytesValue marshal(Object value);

  <T> T unmarshal(BytesValue bytesValue, Class<T> clazz);

}
