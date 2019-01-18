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
import hera.api.model.PeerId;
import hera.api.model.VotingInfo;
import java.net.UnknownHostException;
import org.junit.Test;
import types.Rpc;

public class VotingInfoConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() throws UnknownHostException {
    final ModelConverter<VotingInfo, Rpc.Vote> converter =
        new VotingInfoConverterFactory().create();

    final Rpc.Vote rpcVotingInfo = Rpc.Vote.newBuilder()
        .setCandidate(copyFrom(new PeerId(of(randomUUID().toString().getBytes())).getBytesValue()))
        .setAmount(copyFrom(Aer.AERGO_ONE))
        .build();

    final VotingInfo actualDomainVotingInfo = converter.convertToDomainModel(rpcVotingInfo);
    assertNotNull(actualDomainVotingInfo);
  }

}
