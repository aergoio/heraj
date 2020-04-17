package hera.api.transaction.dsl;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.List;

@ApiAudience.Public
@ApiStability.Unstable
public interface VoteTransaction extends AergoTransaction {

  interface WithNothing extends NeedChainIdHash<WithChainIdHash> {

  }

  interface WithChainIdHash extends NeedSender<WithChainIdHashAndSender> {

  }

  interface WithChainIdHashAndSender {

    /**
     * Accept a vote id. (eg. voteBP).
     *
     * @param voteId a vote id
     * @return next state after accepting vote id
     */
    WithChainIdHashAndSenderAndVoteId voteId(String voteId);
  }

  interface WithChainIdHashAndSenderAndVoteId {

    /**
     * Accept candidates to be voted.
     *
     * @param candidates candidates to be voted
     * @return next state after accepting candidates
     */
    WithChainIdHashAndSenderAndVoteIdAndCandidates candidates(List<String> candidates);
  }

  interface WithChainIdHashAndSenderAndVoteIdAndCandidates extends NeedNonce<WithReady> {

  }

  interface WithReady extends BuildReady {

  }

}
