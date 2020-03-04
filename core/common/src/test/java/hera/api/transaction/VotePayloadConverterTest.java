/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.BytesValue;
import hera.api.model.Vote;
import java.util.Arrays;
import org.junit.Test;

public class VotePayloadConverterTest extends AbstractTestCase {

  @Test
  public void testConvertToPayload() {
    final PayloadConverter<Vote> converter = new VotePayloadConverter();
    final Vote vote = Vote.newBuilder()
        .voteId(randomUUID().toString())
        .candidates(Arrays.asList(new String[] {randomUUID().toString(), randomUUID().toString()}))
        .build();
    final BytesValue payload = converter.convertToPayload(vote);
    assertNotNull(payload);
  }

}
