/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.internal;

import static hera.util.ValidationUtils.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.protobuf.ByteString;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.function.Function3;
import hera.api.function.Function4;
import hera.api.model.Account;
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
import hera.client.ChannelInjectable;
import hera.exception.TransactionVerificationException;
import hera.key.Signer;
import hera.key.TxSigner;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.AccountStateConverterFactory;
import hera.transport.AccountTotalVoteConverterFactory;
import hera.transport.ElectedCandidateConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.StakeInfoConverterFactory;
import hera.transport.TransactionConverterFactory;
import io.grpc.ManagedChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
public class AccountBaseTemplate implements ChannelInjectable, ContextProviderInjectable {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<AccountState, Blockchain.State> accountStateConverter =
      new AccountStateConverterFactory().create();

  protected final ModelConverter<StakeInfo, Rpc.Staking> stakingInfoConverter =
      new StakeInfoConverterFactory().create();

  protected final ModelConverter<Transaction, Blockchain.Tx> transactionConverter =
      new TransactionConverterFactory().create();

  protected final ModelConverter<ElectedCandidate, Rpc.Vote> electedCandidateConverter =
      new ElectedCandidateConverterFactory().create();

  protected final ModelConverter<AccountTotalVote, Rpc.AccountVoteInfo> accountTotalVoteConverter =
      new AccountTotalVoteConverterFactory().create();

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  protected ContextProvider contextProvider;

  protected TransactionBaseTemplate transactionBaseTemplate = new TransactionBaseTemplate();

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    this.transactionBaseTemplate.setContextProvider(contextProvider);
  }

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
    this.transactionBaseTemplate.setChannel(channel);
  }

  @Getter
  private final Function1<AccountAddress, Future<AccountState>> stateFunction =
      new Function1<AccountAddress, Future<AccountState>>() {

        @Override
        public Future<AccountState> apply(final AccountAddress address) {
          logger.debug("GetState with {}", address);

          final Rpc.SingleBytes rpcAddress = Rpc.SingleBytes.newBuilder()
              .setValue(accountAddressConverter.convertToRpcModel(address))
              .build();
          logger.trace("AergoService getstate arg: {}", rpcAddress);

          final Future<Blockchain.State> rawFuture = aergoService.getState(rpcAddress);
          final Future<AccountState> convertedFuture =
              HerajFutures.transform(rawFuture, new Function1<Blockchain.State, AccountState>() {

                @Override
                public AccountState apply(final Blockchain.State state) {
                  final AccountState withoutAddress =
                      accountStateConverter.convertToDomainModel(state);
                  return AccountState.newBuilder().address(address)
                      .nonce(withoutAddress.getNonce())
                      .balance(withoutAddress.getBalance())
                      .build();
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function3<Signer, String, Long, Future<TxHash>> createNameFunction =
      new Function3<Signer, String, Long, Future<TxHash>>() {

        @Override
        public Future<TxHash> apply(final Signer signer, final String name,
            final Long nonce) {
          logger.debug("Create account name with signer: {}, name: {}, nonce: {}",
              signer.getPrincipal(), name, nonce);

          final RawTransaction rawTransaction = RawTransaction.newCreateNameTxBuilder()
              .chainIdHash(contextProvider.get().getChainIdHash())
              .from(signer.getPrincipal())
              .nonce(nonce)
              .name(name)
              .build();
          final Transaction signed = signer.sign(rawTransaction);
          return transactionBaseTemplate.getCommitFunction().apply(signed);
        }
      };

  @Getter
  private final Function4<Signer, String, AccountAddress, Long,
      Future<TxHash>> updateNameFunction = new Function4<Signer, String,
          AccountAddress, Long, Future<TxHash>>() {

        @Override
        public Future<TxHash> apply(final Signer signer, final String name,
            final AccountAddress newOwner, final Long nonce) {
          logger.debug("Update account name with signer: {}, name: {}, to account: {}, nonce: {}",
              signer, name, newOwner, nonce);

          final RawTransaction rawTransaction = RawTransaction.newUpdateNameTxBuilder()
              .chainIdHash(contextProvider.get().getChainIdHash())
              .from(signer.getPrincipal())
              .nonce(nonce)
              .name(name)
              .newOwner(newOwner)
              .build();
          final Transaction signed = signer.sign(rawTransaction);
          return transactionBaseTemplate.getCommitFunction().apply(signed);
        }
      };

  @Getter
  private final Function2<String, Long, Future<AccountAddress>> getNameOwnerFunction =
      new Function2<String, Long, Future<AccountAddress>>() {

        @Override
        public Future<AccountAddress> apply(final String name, final Long blockNumber) {
          logger.debug("Get name owner of name: {}, blockNumber: {}", name, blockNumber);
          assertTrue(blockNumber >= 0, "Block number must >= 0");

          final Rpc.Name rpcName = Rpc.Name.newBuilder()
              .setName(name)
              .setBlockNo(blockNumber)
              .build();
          logger.trace("AergoService getNameInfo arg: {}", rpcName);

          final Future<Rpc.NameInfo> rawFuture = aergoService.getNameInfo(rpcName);
          final Future<AccountAddress> convertedFuture =
              HerajFutures.transform(rawFuture, new Function1<Rpc.NameInfo, AccountAddress>() {

                @Override
                public AccountAddress apply(final Rpc.NameInfo nameInfo) {
                  final AccountAddress converted =
                      accountAddressConverter.convertToDomainModel(nameInfo.getOwner());
                  return BytesValue.EMPTY.equals(converted.getBytesValue()) ? null : converted;
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function3<Signer, Aer, Long, Future<TxHash>> stakingFunction =
      new Function3<Signer, Aer, Long, Future<TxHash>>() {

        @Override
        public Future<TxHash> apply(final Signer signer, final Aer amount,
            final Long nonce) {
          logger.debug("Staking account with signer: {}, amount: {}, nonce: {}",
              signer.getPrincipal(), amount, nonce);

          final RawTransaction rawTransaction = RawTransaction.newStakeTxBuilder()
              .chainIdHash(contextProvider.get().getChainIdHash())
              .from(signer.getPrincipal())
              .amount(amount)
              .nonce(nonce)
              .build();
          final Transaction signed = signer.sign(rawTransaction);
          return transactionBaseTemplate.getCommitFunction().apply(signed);
        }
      };

  @Getter
  private final Function3<Signer, Aer, Long, Future<TxHash>> unstakingFunction =
      new Function3<Signer, Aer, Long, Future<TxHash>>() {

        @Override
        public Future<TxHash> apply(final Signer signer, final Aer amount,
            final Long nonce) {
          logger.debug("Unstaking account with signer: {}, amount: {}, nonce: {}",
              signer.getPrincipal(), amount, nonce);

          final RawTransaction rawTransaction = RawTransaction.newUnstakeTxBuilder()
              .chainIdHash(contextProvider.get().getChainIdHash())
              .from(signer.getPrincipal())
              .amount(amount)
              .nonce(nonce)
              .build();
          final Transaction signed = signer.sign(rawTransaction);
          return transactionBaseTemplate.getCommitFunction().apply(signed);
        }
      };

  @Getter
  private final Function1<AccountAddress, Future<StakeInfo>> stakingInfoFunction =
      new Function1<AccountAddress, Future<StakeInfo>>() {

        @Override
        public Future<StakeInfo> apply(final AccountAddress accountAddress) {
          logger.debug("Get staking information with address: {}", accountAddress);

          final Rpc.AccountAddress rpcAddress = Rpc.AccountAddress.newBuilder()
              .setValue(accountAddressConverter.convertToRpcModel(accountAddress))
              .build();
          logger.trace("AergoService getStaking arg: {}", rpcAddress);

          final Future<Rpc.Staking> rawFuture = aergoService.getStaking(rpcAddress);
          final Future<StakeInfo> convertedFuture =
              HerajFutures.transform(rawFuture, new Function1<Rpc.Staking, StakeInfo>() {

                @Override
                public StakeInfo apply(final Rpc.Staking rpcStaking) {
                  final StakeInfo withoutAddress =
                      stakingInfoConverter.convertToDomainModel(rpcStaking);
                  return StakeInfo.newBuilder().address(accountAddress)
                      .amount(withoutAddress.getAmount())
                      .blockNumber(withoutAddress.getBlockNumber())
                      .build();
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function2<Account, RawTransaction, Future<Transaction>> signFunction =
      new Function2<Account, RawTransaction, Future<Transaction>>() {

        @Override
        public Future<Transaction> apply(final Account account,
            final RawTransaction rawTransaction) {
          logger.debug("Sign request with account: {}, rawTx: {}", account.getAddress(),
              rawTransaction);

          if (account instanceof TxSigner) {
            final TxSigner signer = (TxSigner) account;
            final Transaction signed = signer.sign(rawTransaction);
            return HerajFutures.success(signed);
          } else {
            final Transaction domainTransaction = Transaction.newBuilder()
                .rawTransaction(rawTransaction)
                .build();
            final Blockchain.Tx rpcTx = transactionConverter.convertToRpcModel(domainTransaction);
            logger.trace("AergoService signTX arg: {}", rpcTx);

            final Future<Blockchain.Tx> rawFuture = aergoService.signTX(rpcTx);
            final Future<Transaction> convertedFuture =
                HerajFutures.transform(rawFuture, new Function1<Blockchain.Tx, Transaction>() {

                  @Override
                  public Transaction apply(final Blockchain.Tx rpcTx) {
                    return transactionConverter.convertToDomainModel(rpcTx);
                  }
                });
            return convertedFuture;
          }
        }
      };

  @Getter
  private final Function2<Account, Transaction, Future<Boolean>> verifyFunction =
      new Function2<Account, Transaction, Future<Boolean>>() {

        @Override
        public Future<Boolean> apply(final Account account,
            final Transaction transaction) {
          logger.debug("Sign verify with account: {}, signedTx: {}", account, transaction);

          final Blockchain.Tx rpcTx = transactionConverter.convertToRpcModel(transaction);
          logger.trace("AergoService verifyTX arg: {}", rpcTx);

          final Future<Rpc.VerifyResult> rawFuture = aergoService.verifyTX(rpcTx);
          final Future<Boolean> convertedFuture =
              HerajFutures.transform(rawFuture, new Function1<Rpc.VerifyResult, Boolean>() {

                @Override
                public Boolean apply(final Rpc.VerifyResult result) {
                  if (Rpc.VerifyStatus.VERIFY_STATUS_OK != result.getError()) {
                    new TransactionVerificationException(result.getError());
                  }
                  return true;
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function4<Signer, String, List<String>, Long,
      Future<TxHash>> voteFunction = new Function4<
          Signer, String, List<String>, Long, Future<TxHash>>() {

        @Override
        public Future<TxHash> apply(final Signer signer, final String voteId,
            final List<String> candidates, final Long nonce) {
          logger.debug("Voting with signer: }, voteId: {}, candidates: {}, nonce: {}",
              signer.getPrincipal(), voteId, candidates, nonce);

          final RawTransaction rawTransaction = RawTransaction.newVoteTxBuilder()
              .chainIdHash(contextProvider.get().getChainIdHash())
              .from(signer.getPrincipal())
              .nonce(nonce)
              .voteId(voteId)
              .candidates(candidates)
              .build();
          final Transaction signed = signer.sign(rawTransaction);
          return transactionBaseTemplate.getCommitFunction().apply(signed);
        }
      };

  @Getter
  private final Function2<String, Integer,
      Future<List<ElectedCandidate>>> listElectedFunction = new Function2<
          String, Integer, Future<List<ElectedCandidate>>>() {

        @Override
        public Future<List<ElectedCandidate>> apply(final String voteId,
            final Integer showCount) {
          logger.debug("Get votes status with voteId: {}, showCount: {}", voteId, showCount);

          final Rpc.VoteParams rpcVoteParams = Rpc.VoteParams.newBuilder()
              .setId(voteId)
              .setCount(showCount)
              .build();
          logger.trace("AergoService getVotes arg: {}", rpcVoteParams);

          final Future<Rpc.VoteList> rawFuture = aergoService.getVotes(rpcVoteParams);
          final Future<List<ElectedCandidate>> convertedFuture =
              HerajFutures.transform(rawFuture,
                  new Function1<Rpc.VoteList, List<ElectedCandidate>>() {

                    @Override
                    public List<ElectedCandidate> apply(final Rpc.VoteList rpcVoteList) {
                      final List<ElectedCandidate> electedCandidates = new ArrayList<>();
                      for (final Rpc.Vote rpcCandidate : rpcVoteList.getVotesList()) {
                        final ElectedCandidate domainElectedCandidate =
                            electedCandidateConverter.convertToDomainModel(rpcCandidate);
                        electedCandidates.add(domainElectedCandidate);
                      }
                      return electedCandidates;
                    }
                  });
          return convertedFuture;
        }
      };

  @Getter
  private final Function1<AccountAddress, Future<AccountTotalVote>> votesOfFunction =
      new Function1<AccountAddress, Future<AccountTotalVote>>() {

        @Override
        public Future<AccountTotalVote> apply(final AccountAddress accountAddress) {
          logger.debug("Get votes with address: {}", accountAddress);

          final Rpc.AccountAddress rpcAddress = Rpc.AccountAddress.newBuilder()
              .setValue(accountAddressConverter.convertToRpcModel(accountAddress))
              .build();
          logger.trace("AergoService getAccountVotes arg: {}", rpcAddress);

          final Future<Rpc.AccountVoteInfo> rawFuture =
              aergoService.getAccountVotes(rpcAddress);
          final Future<AccountTotalVote> convertedFuture =
              HerajFutures.transform(rawFuture,
                  new Function1<Rpc.AccountVoteInfo, AccountTotalVote>() {

                    @Override
                    public AccountTotalVote apply(final Rpc.AccountVoteInfo rpcAccountVoteTotal) {
                      return accountTotalVoteConverter.convertToDomainModel(rpcAccountVoteTotal);
                    }
                  });
          return convertedFuture;
        }
      };

}
