# Common

Common module contains basic api including model, key, context, encoding, decoding, strategies and exceptions.

## Model

Describe basic models of blockchain.

#### Account related

* Account
* AccountAddress
* Identity
* Authentication
* AccountState
* EncryptedPrivateKey
* AergoKey
* Aer
* Fee

#### Block related

* Block
* BlockHash
* BlockHeader
* BlockChainStatus

#### Blockchain related

* NodeStatus
* ModuleStatus
* Peer
* PeerId
* PeerMetric
* StakeInfo
* ElectedCandidate
* AccountTotalVote
* VoteInfo
* ChainId
* ChainInfo
* ChainIdHash

#### Transaction related

* RawTransaction
* Transaction
* TxHash
* Signature

#### Contract related

* ContractAddress
* ContractTxHash
* ContractTxReceipt
* ContractInterface
* ContractDefinition
* ContractFunction
* ContractInvocation
* ContractResult
* Event
* EventFilter

#### Streaming

* StreamObserver
* Subscription

### Internal

Internal models are used inside of api.

## Strategy

Strategy is used for any customizable operation. It is binded with context and any operation holding context can use strategy. There are 3 type of base strategy:

* PreInvocationStrategy : strategy which applied before invocation of each operation
* OnInvocationStrategy : strategy which applied before and after invocation of each operation
* PostInvocationStrategy : strategy which applied after operation finishs successfully

## Context

Context is a context holding invocation info, strategy, etc.