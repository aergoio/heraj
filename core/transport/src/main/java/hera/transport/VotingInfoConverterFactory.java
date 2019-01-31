/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.parseToAer;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.PeerId;
import hera.api.model.VotingInfo;
import org.slf4j.Logger;
import types.Rpc;

public class VotingInfoConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<VotingInfo, Rpc.Vote> domainConverter =
      new Function1<VotingInfo, Rpc.Vote>() {

        @Override
        public Rpc.Vote apply(final VotingInfo domainVote) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Rpc.Vote, VotingInfo> rpcConverter =
      new Function1<Rpc.Vote, VotingInfo>() {

        @Override
        public VotingInfo apply(final Rpc.Vote rpcVotingInfo) {
          logger.trace("Rpc vote status to convert: {}", rpcVotingInfo);
          final VotingInfo domainVotingInfo = new VotingInfo(
              new PeerId(of(rpcVotingInfo.getCandidate().toByteArray())),
              parseToAer(rpcVotingInfo.getAmount()));
          logger.trace("Domain vote status converted: {}", domainVotingInfo);
          return domainVotingInfo;
        }
      };

  public ModelConverter<VotingInfo, Rpc.Vote> create() {
    return new ModelConverter<VotingInfo, Rpc.Vote>(domainConverter, rpcConverter);
  }

}
