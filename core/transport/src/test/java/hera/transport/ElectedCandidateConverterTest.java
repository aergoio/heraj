/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Aer;
import hera.api.model.ElectedCandidate;
import java.net.UnknownHostException;
import org.junit.Test;
import types.Rpc;

public class ElectedCandidateConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() throws UnknownHostException {
    final ModelConverter<ElectedCandidate, Rpc.Vote> converter =
        new ElectedCandidateConverterFactory().create();

    final Rpc.Vote rpcElectedCandidate = Rpc.Vote.newBuilder()
        .setCandidate(copyFrom(of(randomUUID().toString().getBytes())))
        .setAmount(copyFrom(Aer.AERGO_ONE))
        .build();

    final ElectedCandidate actualDomainElectedCandidate =
        converter.convertToDomainModel(rpcElectedCandidate);
    assertNotNull(actualDomainElectedCandidate);
  }

}
