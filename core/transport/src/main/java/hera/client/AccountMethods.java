/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.RequestMethod;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.ElectedCandidate;
import hera.api.model.RawTransaction;
import hera.api.model.StakeInfo;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.transaction.CreateNameTransactionBuilder;
import hera.api.transaction.StakeTransactionBuilder;
import hera.api.transaction.UnStakeTransactionBuilder;
import hera.api.transaction.UpdateNameTransactionBuilder;
import hera.api.transaction.VoteTransactionBuilder;
import hera.key.Signer;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.AccountStateConverterFactory;
import hera.transport.AccountTotalVoteConverterFactory;
import hera.transport.ElectedCandidateConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.StakeInfoConverterFactory;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import types.Blockchain;
import types.Rpc;

class AccountMethods extends AbstractMethods {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<AccountState, Blockchain.State> accountStateConverter =
      new AccountStateConverterFactory().create();

  protected final ModelConverter<StakeInfo, Rpc.Staking> stakingInfoConverter =
      new StakeInfoConverterFactory().create();

  protected final ModelConverter<ElectedCandidate, Rpc.Vote> electedCandidateConverter =
      new ElectedCandidateConverterFactory().create();

  protected final ModelConverter<AccountTotalVote, Rpc.AccountVoteInfo> accountTotalVoteConverter =
      new AccountTotalVoteConverterFactory().create();

  protected final TransactionMethods transactionMethods = new TransactionMethods();

  @Getter
  protected final RequestMethod<AccountState> accountState = new RequestMethod<AccountState>() {

    @Getter
    protected final String name = "stateFunction";

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, AccountAddress.class);
    }

    @Override
    protected AccountState runInternal(final List<Object> parameters) throws Exception {
      final AccountAddress accountAddress = (AccountAddress) parameters.get(0);
      logger.debug("GetState with address: {}", accountAddress);

      final Rpc.SingleBytes rpcAddress = Rpc.SingleBytes.newBuilder()
          .setValue(accountAddressConverter.convertToRpcModel(accountAddress))
          .build();
      logger.trace("AergoService getstate arg: {}", rpcAddress);

      final Blockchain.State rpcState = getBlockingStub().getState(rpcAddress);
      final AccountState withoutAddress = accountStateConverter.convertToDomainModel(rpcState);
      return AccountState.newBuilder().address(accountAddress)
          .nonce(withoutAddress.getNonce())
          .balance(withoutAddress.getBalance())
          .build();
    }
  };

  @Getter
  protected final RequestMethod<TxHash> createName = new RequestMethod<TxHash>() {

    @Getter
    protected final String name = "createName";

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, Signer.class);
      validateType(parameters, 1, String.class);
      validateType(parameters, 2, Long.class);
    }

    @Override
    protected TxHash runInternal(final List<Object> parameters) throws Exception {
      final Signer signer = (Signer) parameters.get(0);
      final String name = (String) parameters.get(1);
      final Long nonce = (Long) parameters.get(2);
      logger.debug("Create account name with signer: {}, name: {}, nonce: {}", signer, name, nonce);

      final RawTransaction rawTransaction = new CreateNameTransactionBuilder()
          .chainIdHash(getChainIdHash())
          .from(signer.getPrincipal())
          .name(name)
          .nonce(nonce)
          .build();
      final Transaction signed = signer.sign(rawTransaction);
      return transactionMethods.getCommit().invoke(Arrays.<Object>asList(signed));
    }
  };

  @Getter
  protected final RequestMethod<TxHash> updateName = new RequestMethod<TxHash>() {

    @Getter
    protected final String name = "updateName";

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, Signer.class);
      validateType(parameters, 1, String.class);
      validateType(parameters, 2, AccountAddress.class);
      validateType(parameters, 3, Long.class);
    }

    @Override
    protected TxHash runInternal(final List<Object> parameters) throws Exception {
      final Signer signer = (Signer) parameters.get(0);
      final String name = (String) parameters.get(1);
      final AccountAddress newOwner = (AccountAddress) parameters.get(2);
      final Long nonce = (Long) parameters.get(3);
      logger.debug("Update account name with signer: {}, name: {}, newOwner: {}, nonce: {}", signer,
          name, newOwner, nonce);

      final RawTransaction rawTransaction = new UpdateNameTransactionBuilder()
          .chainIdHash(getChainIdHash())
          .from(signer.getPrincipal())
          .name(name)
          .nextOwner(newOwner)
          .nonce(nonce)
          .build();
      final Transaction signed = signer.sign(rawTransaction);
      return transactionMethods.getCommit().invoke(Arrays.<Object>asList(signed));
    }

  };

  @Getter
  protected final RequestMethod<AccountAddress> nameOwner = new RequestMethod<AccountAddress>() {

    @Getter
    protected final String name = "getNameOwner";

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, String.class);
      validateType(parameters, 1, Long.class);
      validateValue(((Long) parameters.get(1)) >= 0, "Block number must >= 0");
    }

    @Override
    protected AccountAddress runInternal(final List<Object> parameters) throws Exception {
      logger.debug("Get name owner with {}", parameters);
      final String name = (String) parameters.get(0);
      final Long blockNumber = (Long) parameters.get(1);

      final Rpc.Name rpcName = Rpc.Name.newBuilder()
          .setName(name)
          .setBlockNo(blockNumber)
          .build();
      logger.trace("AergoService getNameInfo arg: {}", rpcName);

      final Rpc.NameInfo rpcNameInfo = getBlockingStub().getNameInfo(rpcName);
      final AccountAddress converted = accountAddressConverter
          .convertToDomainModel(rpcNameInfo.getOwner());
      return BytesValue.EMPTY.equals(converted.getBytesValue()) ? null : converted;
    }

  };

  @Getter
  protected final RequestMethod<TxHash> stake = new RequestMethod<TxHash>() {

    @Getter
    protected final String name = "stake";

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, Signer.class);
      validateType(parameters, 1, Aer.class);
      validateType(parameters, 2, Long.class);
    }

    @Override
    protected TxHash runInternal(final List<Object> parameters) throws Exception {
      logger.debug("Staking account with {}", parameters);
      final Signer signer = (Signer) parameters.get(0);
      final Aer amount = (Aer) parameters.get(1);
      final Long nonce = (Long) parameters.get(2);

      final RawTransaction rawTransaction = new StakeTransactionBuilder()
          .chainIdHash(getChainIdHash())
          .from(signer.getPrincipal())
          .amount(amount)
          .nonce(nonce)
          .build();
      final Transaction signed = signer.sign(rawTransaction);
      return transactionMethods.getCommit().invoke(Arrays.<Object>asList(signed));
    }

  };

  @Getter
  protected final RequestMethod<TxHash> unstake = new RequestMethod<TxHash>() {

    @Getter
    protected final String name = "unstake";

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, Signer.class);
      validateType(parameters, 1, Aer.class);
      validateType(parameters, 2, Long.class);
    }

    @Override
    protected TxHash runInternal(final List<Object> parameters) throws Exception {
      logger.debug("Unstaking account with {}", parameters);
      final Signer signer = (Signer) parameters.get(0);
      final Aer amount = (Aer) parameters.get(1);
      final Long nonce = (Long) parameters.get(2);

      final RawTransaction rawTransaction = new UnStakeTransactionBuilder()
          .chainIdHash(getChainIdHash())
          .from(signer.getPrincipal())
          .amount(amount)
          .nonce(nonce)
          .build();
      final Transaction signed = signer.sign(rawTransaction);
      return transactionMethods.getCommit().invoke(Arrays.<Object>asList(signed));
    }

  };

  @Getter
  protected final RequestMethod<StakeInfo> stakeInfo = new RequestMethod<StakeInfo>() {

    @Getter
    protected final String name = "stakeInfo";

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, AccountAddress.class);
    }

    @Override
    protected StakeInfo runInternal(final List<Object> parameters) throws Exception {
      logger.debug("Get staking information with {}", parameters);
      final AccountAddress accountAddress = (AccountAddress) parameters.get(0);

      final Rpc.AccountAddress rpcAddress = Rpc.AccountAddress.newBuilder()
          .setValue(accountAddressConverter.convertToRpcModel(accountAddress))
          .build();
      logger.trace("AergoService getStaking arg: {}", rpcAddress);

      final Rpc.Staking rpcStakeInfo = getBlockingStub().getStaking(rpcAddress);
      final StakeInfo withoutAddress = stakingInfoConverter.convertToDomainModel(rpcStakeInfo);
      return StakeInfo.newBuilder().address(accountAddress)
          .amount(withoutAddress.getAmount())
          .blockNumber(withoutAddress.getBlockNumber())
          .build();
    }

  };

  @Getter
  protected final RequestMethod<TxHash> vote = new RequestMethod<TxHash>() {

    @Getter
    protected final String name = "vote";

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, Signer.class);
      validateType(parameters, 1, String.class);
      validateType(parameters, 2, List.class);
      validateType(parameters, 3, Long.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected TxHash runInternal(final List<Object> parameters) throws Exception {
      logger.debug("Voting with {}", parameters);
      final Signer signer = (Signer) parameters.get(0);
      final String voteId = (String) parameters.get(1);
      final List<String> candidates = (List<String>) parameters.get(2);
      final Long nonce = (Long) parameters.get(3);

      final RawTransaction rawTransaction = new VoteTransactionBuilder()
          .chainIdHash(getChainIdHash())
          .from(signer.getPrincipal())
          .voteId(voteId)
          .candidates(candidates)
          .nonce(nonce)
          .build();
      final Transaction signed = signer.sign(rawTransaction);
      return transactionMethods.getCommit().invoke(Arrays.<Object>asList(signed));
    }

  };

  @Getter
  protected final RequestMethod<List<ElectedCandidate>> listElected =
      new RequestMethod<List<ElectedCandidate>>() {

        @Getter
        protected final String name = "listElected";

        @Override
        protected void validate(final List<Object> parameters) {
          validateType(parameters, 0, String.class);
          validateType(parameters, 1, Integer.class);
        }

        @Override
        protected List<ElectedCandidate> runInternal(final List<Object> parameters)
            throws Exception {
          logger.debug("Get votes status with {}", parameters);
          final String voteId = (String) parameters.get(0);
          final int showCount = (int) parameters.get(1);

          final Rpc.VoteParams rpcVoteParams = Rpc.VoteParams.newBuilder()
              .setId(voteId)
              .setCount(showCount)
              .build();
          logger.trace("AergoService getVotes arg: {}", rpcVoteParams);

          final Rpc.VoteList rpcVoteList = getBlockingStub().getVotes(rpcVoteParams);
          final List<ElectedCandidate> electedCandidates = new LinkedList<>();
          for (final Rpc.Vote rpcCandidate : rpcVoteList.getVotesList()) {
            final ElectedCandidate domainElectedCandidate =
                electedCandidateConverter.convertToDomainModel(rpcCandidate);
            electedCandidates.add(domainElectedCandidate);
          }
          return electedCandidates;
        }

      };

  @Getter
  protected final RequestMethod<AccountTotalVote> voteOf = new RequestMethod<AccountTotalVote>() {

    @Getter
    protected final String name = "voteOf";

    @Override
    protected void validate(final List<Object> parameters) {
      validateType(parameters, 0, AccountAddress.class);
    }

    @Override
    protected AccountTotalVote runInternal(final List<Object> parameters) throws Exception {
      logger.debug("Get votes with {}", parameters);
      final AccountAddress accountAddress = (AccountAddress) parameters.get(0);

      final Rpc.AccountAddress rpcAddress = Rpc.AccountAddress.newBuilder()
          .setValue(accountAddressConverter.convertToRpcModel(accountAddress))
          .build();
      logger.trace("AergoService getAccountVotes arg: {}", rpcAddress);

      final Rpc.AccountVoteInfo rpcAccountVoteTotal = getBlockingStub().getAccountVotes(rpcAddress);
      return accountTotalVoteConverter.convertToDomainModel(rpcAccountVoteTotal);
    }

  };

}
