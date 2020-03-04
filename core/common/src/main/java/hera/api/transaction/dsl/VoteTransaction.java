package hera.api.transaction.dsl;

import java.util.List;

public interface VoteTransaction extends AergoTransaction {

  interface WithNothing extends NeedChainIdHash<WithChainIdHash> {
  }

  interface WithChainIdHash extends NeedSender<WithChainIdHashAndSender> {
  }

  interface WithChainIdHashAndSender extends NeedNonce<WithChainIdHashAndSenderAndNonce> {
  }

  interface WithChainIdHashAndSenderAndNonce {
    /**
     * Accept a vote id. (eg. voteBP).
     *
     * @param voteId a vote id
     * @return next state after accepting vote id
     */
    WithChainIdHashAndSenderAndNonceAndVoteId voteId(String voteId);
  }

  interface WithChainIdHashAndSenderAndNonceAndVoteId {
    /**
     * Accept candidates to be voted.
     *
     * @param candidates candidates to be voted
     * @return next state after accepting candidates
     */
    WithReady candidates(List<String> candidates);
  }

  interface WithReady extends BuildReady {
  }

}
