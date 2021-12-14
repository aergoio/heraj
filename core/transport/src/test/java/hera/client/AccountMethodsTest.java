/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_CLIENT;
import static hera.client.ClientContextKeys.GRPC_VALUE_CHAIN_ID_HASH_HOLDER;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.protobuf.ByteString;
import hera.*;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.AccountTotalVote;
import hera.api.model.ElectedCandidate;
import hera.api.model.StakeInfo;
import hera.api.model.TxHash;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.ModelConverter;
import java.util.Arrays;
import java.util.List;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;
import types.Blockchain;
import types.Rpc;

@PrepareForTest({AergoRPCServiceBlockingStub.class, AergoRPCServiceStub.class})
public class AccountMethodsTest extends AbstractTestCase {
  @BeforeClass
  public static void beforeClass() throws Exception {
    // powermock cannot mock java.security packages in jdk17 due to stricter security policies
    Assume.assumeTrue(TestUtils.getVersion() < 17 );
  }

  @Test
  public void testAccountState() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.getState(any(Rpc.SingleBytes.class)))
              .thenReturn(Blockchain.State.newBuilder().build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance().withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final AccountMethods accountMethods = new AccountMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyAccountAddress);
          final AccountState accountState = accountMethods.getAccountState()
              .invoke(parameters);
          assertNotNull(accountState);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testCreateNameTx() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.commitTX(any(Blockchain.TxList.class)))
              .thenReturn(Rpc.CommitResultList.newBuilder()
                  .addResults(Rpc.CommitResult.newBuilder().build())
                  .build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final ChainIdHashHolder chainIdHashHolder = new ChainIdHashHolder();
          chainIdHashHolder.put(anyChainIdHash);
          final Context context = EmptyContext.getInstance()
              .withValue(GRPC_CLIENT, mockClient)
              .withValue(GRPC_VALUE_CHAIN_ID_HASH_HOLDER, chainIdHashHolder);
          ContextHolder.attach(context);

          // then
          final AccountMethods accountMethods = new AccountMethods();
          final List<Object> parameters = Arrays.<Object>asList(anySigner, anyName, anyNonce);
          final TxHash txHash = accountMethods.getCreateNameTx()
              .invoke(parameters);
          assertNotNull(txHash);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testUpdateNameTx() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.commitTX(any(Blockchain.TxList.class)))
              .thenReturn(Rpc.CommitResultList.newBuilder()
                  .addResults(Rpc.CommitResult.newBuilder().build())
                  .build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final ChainIdHashHolder chainIdHashHolder = new ChainIdHashHolder();
          chainIdHashHolder.put(anyChainIdHash);
          final Context context = EmptyContext.getInstance()
              .withValue(GRPC_CLIENT, mockClient)
              .withValue(GRPC_VALUE_CHAIN_ID_HASH_HOLDER, chainIdHashHolder);
          ContextHolder.attach(context);

          // then
          final AccountMethods accountMethods = new AccountMethods();
          final List<Object> parameters = Arrays.<Object>asList(anySigner, anyName,
              anyAccountAddress, anyNonce);
          final TxHash txHash = accountMethods.getUpdateNameTx()
              .invoke(parameters);
          assertNotNull(txHash);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testNameOwner() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          final ModelConverter<AccountAddress, ByteString> converter =
              new AccountAddressConverterFactory().create();
          when(mockBlockingStub.getNameInfo(any(Rpc.Name.class)))
              .thenReturn(Rpc.NameInfo.newBuilder()
                  .setOwner(converter.convertToRpcModel(anyAccountAddress))
                  .build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance()
              .withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final AccountMethods accountMethods = new AccountMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyName, anyBlockNumber);
          final AccountAddress accountAddress = accountMethods.getNameOwner()
              .invoke(parameters);
          assertNotNull(accountAddress);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testStakeTx() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.commitTX(any(Blockchain.TxList.class)))
              .thenReturn(Rpc.CommitResultList.newBuilder()
                  .addResults(Rpc.CommitResult.newBuilder().build())
                  .build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final ChainIdHashHolder chainIdHashHolder = new ChainIdHashHolder();
          chainIdHashHolder.put(anyChainIdHash);
          final Context context = EmptyContext.getInstance()
              .withValue(GRPC_CLIENT, mockClient)
              .withValue(GRPC_VALUE_CHAIN_ID_HASH_HOLDER, chainIdHashHolder);
          ContextHolder.attach(context);

          // then
          final AccountMethods accountMethods = new AccountMethods();
          final List<Object> parameters = Arrays.<Object>asList(anySigner, anyAmount, anyNonce);
          final TxHash txHash = accountMethods.getStakeTx()
              .invoke(parameters);
          assertNotNull(txHash);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testUnstakeTx() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.commitTX(any(Blockchain.TxList.class)))
              .thenReturn(Rpc.CommitResultList.newBuilder()
                  .addResults(Rpc.CommitResult.newBuilder().build())
                  .build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final ChainIdHashHolder chainIdHashHolder = new ChainIdHashHolder();
          chainIdHashHolder.put(anyChainIdHash);
          final Context context = EmptyContext.getInstance()
              .withValue(GRPC_CLIENT, mockClient)
              .withValue(GRPC_VALUE_CHAIN_ID_HASH_HOLDER, chainIdHashHolder);
          ContextHolder.attach(context);

          // then
          final AccountMethods accountMethods = new AccountMethods();
          final List<Object> parameters = Arrays.<Object>asList(anySigner, anyAmount, anyNonce);
          final TxHash txHash = accountMethods.getUnstakeTx()
              .invoke(parameters);
          assertNotNull(txHash);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testStakeInfo() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.getStaking(any(Rpc.AccountAddress.class)))
              .thenReturn(Rpc.Staking.newBuilder().build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance()
              .withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final AccountMethods accountMethods = new AccountMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyAccountAddress);
          final StakeInfo stakeInfo = accountMethods.getStakeInfo()
              .invoke(parameters);
          assertNotNull(stakeInfo);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testVoteTx() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.commitTX(any(Blockchain.TxList.class)))
              .thenReturn(Rpc.CommitResultList.newBuilder()
                  .addResults(Rpc.CommitResult.newBuilder().build())
                  .build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final ChainIdHashHolder chainIdHashHolder = new ChainIdHashHolder();
          chainIdHashHolder.put(anyChainIdHash);
          final Context context = EmptyContext.getInstance()
              .withValue(GRPC_CLIENT, mockClient)
              .withValue(GRPC_VALUE_CHAIN_ID_HASH_HOLDER, chainIdHashHolder);
          ContextHolder.attach(context);

          // then
          final AccountMethods accountMethods = new AccountMethods();
          final List<Object> parameters = Arrays.<Object>asList(anySigner, anyVoteId, anyCandidates,
              anyNonce);
          final TxHash txHash = accountMethods.getVoteTx()
              .invoke(parameters);
          assertNotNull(txHash);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testListElected() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.getVotes(any(Rpc.VoteParams.class)))
              .thenReturn(Rpc.VoteList.newBuilder()
                  .addVotes(Rpc.Vote.newBuilder().build())
                  .build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance()
              .withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final AccountMethods accountMethods = new AccountMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyVoteId, anySize);
          final List<ElectedCandidate> electedCandidates = accountMethods.getListElected()
              .invoke(parameters);
          assertNotNull(electedCandidates);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testVoteOf() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        try {
          // given
          final AergoRPCServiceBlockingStub mockBlockingStub = mock(
              AergoRPCServiceBlockingStub.class);
          when(mockBlockingStub.getAccountVotes(any(Rpc.AccountAddress.class)))
              .thenReturn(Rpc.AccountVoteInfo.newBuilder().build());
          final GrpcClientImpl mockClient = mock(GrpcClientImpl.class);
          when(mockClient.getBlockingStub()).thenReturn(mockBlockingStub);
          final Context context = EmptyContext.getInstance()
              .withValue(GRPC_CLIENT, mockClient);
          ContextHolder.attach(context);

          // then
          final AccountMethods accountMethods = new AccountMethods();
          final List<Object> parameters = Arrays.<Object>asList(anyAccountAddress);
          final AccountTotalVote accountTotalVote = accountMethods.getVoteOf()
              .invoke(parameters);
          assertNotNull(accountTotalVote);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

}
