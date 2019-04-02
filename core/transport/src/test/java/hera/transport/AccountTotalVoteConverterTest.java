/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.AccountTotalVote;
import java.net.UnknownHostException;
import org.junit.Test;
import types.Rpc;

public class AccountTotalVoteConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() throws UnknownHostException {
    final ModelConverter<AccountTotalVote, Rpc.AccountVoteInfo> converter =
        new AccountTotalVoteConverterFactory().create();

    final Rpc.AccountVoteInfo rpcAccountVoteTotal = Rpc.AccountVoteInfo.newBuilder().build();

    final AccountTotalVote actualDomainVotingInfo =
        converter.convertToDomainModel(rpcAccountVoteTotal);
    assertNotNull(actualDomainVotingInfo);
  }

}
