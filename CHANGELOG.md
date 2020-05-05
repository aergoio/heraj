# Change Log

## Next

- Compatibility
  - Aergo : TBD
  - Protobuf : TBD
- Fixes & Improvements
  - Add KeyFormat
  - Improvement context structure
  - Add sendTx api
  - Split TxReceipt from ContractTxReceipt
  - Add functionName to ContractInvocation
  - Add Name object related apis
- Changes
  - Replace subscribeNewBlockXXX with subscribeBlockXXX
  - Clarify WalletApi, ContractApi
  - Deprecate ContractTxHash
  - Return null on some query methods

## v1.4.0 (Mar 10, 2020)

- Compatibility
  - Aergo : v2.2.x
  - Protobuf : [v2.2.0](https://github.com/aergoio/aergo-protobuf/tree/v2.2.0)
- Features
  - Support for keystore
- Changes
  - Set SimpleNonceProvider default capacity to 1000

## v1.3.0 (Nov 28, 2019)

- Compatibility
  - Aergo : v2.0.x
  - Protobuf : [38d6a4fce32395997efbd66d0c61edab55c8e4a2](https://github.com/aergoio/aergo-protobuf/tree/38d6a4fce32395997efbd66d0c61edab55c8e4a2)
- Features
  - Add ContractApi using WalletApi
  - Support new tx type (fee delegation, transfer, call, deploy)
  - Support fee gas limit
- Fixes & Improvements
  - Fix aer operation on empty one
  - Fix jks password error
- Changes
  - Deprecate WalletFactory (use WalletApiFactory instead)

## v1.2.2 (Nov 13, 2019)

- Compatibility
  - Aergo : v1.3.x
  - Protobuf : [v1.3.0](https://github.com/aergoio/aergo-protobuf/tree/v1.3.0)
- Features
  - Introduce TransactionInfoExtractor
- Fixes & Improvements
  - Fix timeout logging bug
- Changes
  - Remove unlock/lock of KeyStore interface

## v1.2.1 (Oct 4, 2019)

- Compatibility
  - Aergo : v1.2.x
  - Protobuf : [v1.2.0](https://github.com/aergoio/aergo-protobuf/tree/v1.2.0)
- New Features
  - Generate aergokey with a seed
- Fixes & Improvements
  - Check mempool first when fetching tx info
  - Add encode/decode type for signed message
  - Support synchronized lock/unlock on keystore
  - Fix bug in keystore
  - Fix bug in strategy applier

## v1.2.0 (Sep 24, 2019)

- Compatibility
  - Aergo : v1.2.x
  - Protobuf : [v1.2.0](https://github.com/aergoio/aergo-protobuf/tree/v1.2.0)
- Features
  - Support for tls
  - Introduce WalletApi
  - ReDeploy Api (enterprise only)
- Fixes & Improvements
  - Support nested event arguments
  - Improve context structure
  - Fix not connecting chainIdHash from base thread error
- Changes
  - Deprecate Wallet Interface (use WalletApi instead)

## v1.1.0 (Jun 10, 2019)

- Compatibility
  - Aergo : v1.1.x
  - Protobuf : [v1.1.0](https://github.com/aergoio/aergo-protobuf/tree/v1.1.0)
- Features
  - Add state variable to contract interface
  - Add version to peer
  - Add getting chain stats rpc
  - Add amount to ContractDefinition, ContractInvocation
  - Introduce block, block metadata streaming rpc
  - Introduce NonceProvider
  - Introduce TransactionBuilder for each type
  - Introduce Signer, TxSigner
- Changes
  - Remove fetchng block header, Instead, fetch block metadata
  - Deprecate Account Interface
  - Now ---Operations which make transaction take Signer, Not Account
  - KeyStoreOperatoin::create, KeyStoreOperation::importKey return AccountAddress, not Account

## v1.0.1 (Apr 24, 2019)

- Compatibility
  - Aergo : v1.0.x
  - Protobuf : [v1.0.0](https://github.com/aergoio/aergo-protobuf/tree/v1.0.0)
- Fixes & Improvements
  - Fix bug in signing big message
- Changes
  - Clarify sign to hash (sign) and sign to message (signMessage)

## v1.0.0 (Apr 18, 2019)

- Compatibility
  - Aergo : v1.0.x
  - Protobuf : [v1.0.0](https://github.com/aergoio/aergo-protobuf/tree/v1.0.0)
- Features
  - Add api without fee
  - Introduce chainIdHash to transaction
  - Add view, payable type to contract function
- Fixes & Improvements
  - Add apis without fee
  - Separate signer with verifier
- Changes
  - Voting / Staking related api has been changed
  - withNonceRefresh -> withRefresh

## v0.12.3 (Mar 28, 2019)

- Compatibility
  - Aergo : v0.12.x
  - Protobuf : [v0.12.0](https://github.com/aergoio/aergo-protobuf/tree/v0.12.0)
- Fixes & Improvements
  - Implement unimplemented filter with arguments
  - Fix throwing error when on corresponding function in ContractInvocationBuilder

## v0.12.2 (Mar 19, 2019)

- Compatibility
  - Aergo : v0.12.x
  - Protobuf : [v0.12.0](https://github.com/aergoio/aergo-protobuf/tree/v0.12.0)
- Fixes & Improvements
  - Fix frequently sending keep-alive ping
  - Fix getState error when unlock account
  - Convert exception in streaming to high-level one

## v0.12.1 (Mar 15, 2019)

- Compatibility
  - Aergo : v0.12.x
  - Protobuf : [v0.12.0](https://github.com/aergoio/aergo-protobuf/tree/v0.12.0)
- Fixes & Improvements
  - Fix bug in converting contract invocation to json payload form

## v0.12.0 (Mar 8, 2019)

- Compatibility
  - Aergo : v0.12.x
  - Protobuf : [v0.12.0](https://github.com/aergoio/aergo-protobuf/tree/v0.12.0)
- Features
  - Identity interface (for keystore alias)
  - listPeers(showHidden: boolean, showSelf: boolean): List<Peer>
  - lockAccount(authentication: Authentication): Account
  - Event related api (listEvents, subscribeEvent)
  - signMessage / verifyMessage to AergoKey
- Fixes & Improvements
  - Clarify exception of wallet
  - Java keystore alias can be any lowercase string value
  - Fix converting null amount on empty ByteString (should be 0 aergo)
- Changes
  - Wallet::storeKeyStore isn't return boolean value. It throws exception on error
  - Remove some deprecated apis

## v0.11.0 (Feb 14, 2019)

- Compatibility
  - Aergo : v0.11.x
  - Protobuf : [v0.11.0](https://github.com/aergoio/aergo-protobuf/tree/v0.11.0)
- Features
  - Get keystore addresses in wallet
  - getBlockHeader
  - getChainInfo
- Fixes & Improvements
  - Resolve potential memory leak in keystore
  - Fix throwing invalid exception on no connection
- Changes
  - Clarify getNonce of account (getNonce -> getRecentlyUsedNonce)

## v0.10.0 (Jan 25, 2019)

- Compatibility
  - Aergo : v0.10.x
  - Protobuf : [v0.10.0](https://github.com/aergoio/aergo-protobuf/tree/v0.10.0)
- Features
  - Staking related api : stake/unstake/getStakingInfo
  - Voting related api : vote/listVotesOf/listElectedBlockProducers
- Fixes & Improvements
  - Support for JDK 6, 7
  - Support for Android (API 11 or higher)
  - RawTransactionBuilder can call build without setting fee
  - Validate smart contract payload on creation
  - Fix fee rpc to model convertion problem
- Changes
  - Remove AergoEitherClient, AerogAsyncClient related api.
  - Type change : Peer::peerId (String -> PeerId)
  - Wallet : getCurrentAccount -> getAccount, getCurrentAccountState -> getAccountState
  - Creating/Updating name should pay 1 aergo (server policy)

## v0.9 (Dec 26, 2018)

- Compatibility
  - Aergo : v0.9.0
  - Protobuf : [v0.9.0](https://github.com/aergoio/aergo-protobuf/tree/v0.9.0)
- Features
  - Naming to account address
  - Support AER, GAER, AERGO unit
  - ContractDefinition.Builder
  - ContractInvocation.Builder in ContractInterface
  - RawTransaction.Builder
  - Wallet
    - Naive
    - Secure
    - ServerKeyStore
  - SmartContractClient
- Fixes & Improvements
  - Bug fix in FutureFunctionChain
  - Make models immutable
  - Improve user experience
  - Improve context class
- Changes
  - AergoKeyGenerator don't throws checked exception
  - Split KeyStoreOperation from AccountOperation
  - AccountOperation.sign accepts RawTransaction
  - Explicit nonce setting to ContractOperation
  - No explicit lambda in high level api

## v0.8 (Oct 24, 2018)

- Compatibility
  - Aergo : v0.8.0
  - Protobuf : [v0.8.0](https://github.com/aergoio/aergo-protobuf/tree/v0.8.0)
- Features
  - Introduce Three type of basic clients
    - AergoClient
    - AergoEitherClient
    - AergoAsyncClient
  - Introduce Operations
    - AccountOperation
    - BlockOperation
    - BlockchainOperation
    - TransactionOperation
    - ContractOperation
  - Introduce ResultOrError, ResultOrErrorFuture data structure
  - Introduce Context & Strategies
    - NettyConnectStrategy
    - SimpleTimeoutStrategy
  - Introduce AergoKey
