/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.AccountTotalVote;
import hera.api.model.StakeInfo;
import hera.api.model.VoteInfo;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import types.Rpc;

public class AccountTotalVoteConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<StakeInfo, Rpc.Staking> stakeInfoConverterFactory =
      new StakeInfoConverterFactory().create();

  protected final Function1<AccountTotalVote, Rpc.AccountVoteInfo> domainConverter =
      new Function1<AccountTotalVote, Rpc.AccountVoteInfo>() {

        @Override
        public Rpc.AccountVoteInfo apply(final AccountTotalVote domainAccountVoteTotal) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Rpc.AccountVoteInfo, AccountTotalVote> rpcConverter =
      new Function1<Rpc.AccountVoteInfo, AccountTotalVote>() {

        @Override
        public AccountTotalVote apply(final Rpc.AccountVoteInfo rpcAccountVoteTotal) {
          logger.trace("Rpc vote total to convert: {}", rpcAccountVoteTotal);

          final List<VoteInfo> domainVoteInfos = new ArrayList<VoteInfo>();
          for (final Rpc.VoteInfo rpcVoteInfo : rpcAccountVoteTotal.getVotingList()) {
            final List<String> domainCandidates = new ArrayList<String>();
            for (final String rpcCandidate : rpcVoteInfo.getCandidatesList()) {
              domainCandidates.add(rpcCandidate);
            }
            final VoteInfo voteInfo = VoteInfo.newBuilder()
                .voteId(rpcVoteInfo.getId())
                .candidateIds(domainCandidates)
                .build();
            domainVoteInfos.add(voteInfo);
          }

          final StakeInfo domainStakeInfo =
              stakeInfoConverterFactory.convertToDomainModel(rpcAccountVoteTotal.getStaking());
          final AccountTotalVote domainAccountVoteTotal = AccountTotalVote.newBuilder()
              .stakeInfo(domainStakeInfo)
              .voteInfos(domainVoteInfos)
              .build();
          logger.trace("Domain vote status converted: {}", domainAccountVoteTotal);
          return domainAccountVoteTotal;
        }
      };

  public ModelConverter<AccountTotalVote, Rpc.AccountVoteInfo> create() {
    return new ModelConverter<AccountTotalVote, Rpc.AccountVoteInfo>(domainConverter, rpcConverter);
  }

}
