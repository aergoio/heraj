# Transport

Transport module is intended for communication with aergo chain. We use grpc for communication.

## Client

There are 3 type of aergo client:
* AergoClient
* AergoEitherClient
* AergoAsyncClient

### AergoClient

`AergoClient` is a simple client with a context. It has 5 type of operations:
* AccountOperation
* BlockOperation
* BlockchainOperation
* TransactionOperation
* ContractOperation

`AccountOperation` performs account related operations. eg. lock, unlock, create, getstate, sign, ...  
`BlockOperation` performs block related operations. eg. getBlock, listBlockHeaders, ...  
`BlockchainOperation` performs node related operations. eg. getBlockchainStatus, listPeers, getNodeStatus, ...  
`TransactionOperation` performs transaction related operations. eg. commit, getTransaction, ...  
`ContractOperation` performs contract related operations. eg. deploy, getReceipt, execute, query, ...


### AergoEitherClient

`AergoEitherClient` is a functional client with a context. It's similar with `AergoClient`. Only difference is that it handles error by `ResultOrError` and it can chain the function call. It has 5 type of operations:
* AccountEitherOperation
* BlockEitherOperation
* BlockchainEitherOperation
* TransactionEitherOperation
* ContractEitherOperation

`AccountEitherOperation` performs account related operations. eg. lock, unlock, create, getstate, sign, ...  
`BlockEitherOperation` performs block related operations. eg. getBlock, listBlockHeaders, ...  
`BlockchainEitherOperation` performs node related operations. eg. getBlockchainStatus, listPeers, getNodeStatus, ...  
`TransactionEitherOperation` performs transaction related operations. eg. commit, getTransaction, ...  
`ContractEitherOperation` performs contract related operations. eg. deploy, getReceipt, execute, query, ...


### AergoAsyncClient

`AergoAsyncClient` is a async client with a context. It's similar with `AergoEitherClient`. Only difference is that it handles a future object. It has 5 type of operations:
* AccountAsyncOperation
* BlockAsyncOperation
* BlockchainAsyncOperation
* TransactionAsyncOperation
* ContractAsyncOperation

`AccountAsyncOperation` performs account related operations. eg. lock, unlock, create, getstate, sign, ...  
`BlockAsyncOperation` performs block related operations. eg. getBlock, listBlockHeaders, ...  
`BlockchainAsyncOperation` performs node related operations. eg. getBlockchainStatus, listPeers, getNodeStatus, ...  
`TransactionAsyncOperation` performs transaction related operations. eg. commit, getTransaction, ...  
`ContractAsyncOperation` performs contract related operations. eg. deploy, getReceipt, execute, query, ...


## Stragety

We provides basic implementation of strategy.
* NettyConnectStrategy
* SimpleTimeoutStrategy
