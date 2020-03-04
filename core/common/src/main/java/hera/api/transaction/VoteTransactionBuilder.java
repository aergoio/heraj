/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.unmodifiableList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Aer;
import hera.api.model.ChainIdHash;
import hera.api.model.Identity;
import hera.api.model.Name;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction.TxType;
import hera.api.model.Vote;
import hera.api.transaction.dsl.VoteTransaction;
import hera.api.transaction.dsl.VoteTransaction.WithChainIdHash;
import hera.api.transaction.dsl.VoteTransaction.WithChainIdHashAndSender;
import hera.api.transaction.dsl.VoteTransaction.WithChainIdHashAndSenderAndNonce;
import hera.api.transaction.dsl.VoteTransaction.WithChainIdHashAndSenderAndNonceAndVoteId;
import hera.api.transaction.dsl.VoteTransaction.WithReady;
import java.util.LinkedList;
import java.util.List;

@ApiAudience.Public
@ApiStability.Unstable
public class VoteTransactionBuilder implements
    VoteTransaction.WithNothing,
    VoteTransaction.WithChainIdHash,
    VoteTransaction.WithChainIdHashAndSender,
    VoteTransaction.WithChainIdHashAndSenderAndNonce,
    VoteTransaction.WithChainIdHashAndSenderAndNonceAndVoteId,
    VoteTransaction.WithReady {

  protected final PlainTransactionBuilder delegate = new PlainTransactionBuilder();

  protected final PayloadConverter<Vote> payloadConverter = new VotePayloadConverter();

  protected String voteId;

  protected List<String> candidates;

  @Override
  public WithChainIdHash chainIdHash(final ChainIdHash chainIdHash) {
    this.delegate.chainIdHash(chainIdHash);
    return this;
  }

  @Override
  public WithChainIdHashAndSender from(final String senderName) {
    this.delegate.from(senderName);
    return this;
  }

  @Override
  public WithChainIdHashAndSender from(final Identity sender) {
    this.delegate.from(sender);
    return this;
  }

  @Override
  public WithChainIdHashAndSenderAndNonce nonce(long nonce) {
    this.delegate.nonce(nonce);
    return this;
  }

  @Override
  public WithChainIdHashAndSenderAndNonceAndVoteId voteId(final String voteId) {
    assertNotNull(voteId);
    this.voteId = voteId;
    return this;
  }

  @Override
  public WithReady candidates(final List<String> candidates) {
    assertNotNull(candidates);
    this.candidates = unmodifiableList(new LinkedList<>(candidates));
    return this;
  }

  @Override
  public RawTransaction build() {
    this.delegate.to(Name.AERGO_SYSTEM);
    this.delegate.amount(Aer.EMPTY);
    final Vote vote = Vote.newBuilder()
        .voteId(voteId)
        .candidates(candidates)
        .build();
    this.delegate.payload(payloadConverter.convertToPayload(vote));
    this.delegate.type(TxType.GOVERNANCE);
    return delegate.build();
  }

}
