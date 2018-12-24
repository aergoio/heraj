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
* KeyStoreOperation
* BlockOperation
* BlockchainOperation
* TransactionOperation
* ContractOperation

### AergoEitherClient

`AergoEitherClient` is a functional client with a context. It's similar with `AergoClient`. Only difference is that it handles error by `ResultOrError` and it can chain the function call. It has 5 type of operations:
* AccountEitherOperation
* KeyStoreEitherOperation
* BlockEitherOperation
* BlockchainEitherOperation
* TransactionEitherOperation
* ContractEitherOperation

### AergoAsyncClient

`AergoAsyncClient` is a async client with a context. It's similar with `AergoEitherClient`. Only difference is that it handles a future object. It has 5 type of operations:
* AccountAsyncOperation
* KeyStoreAsyncOperation
* BlockAsyncOperation
* BlockchainAsyncOperation
* TransactionAsyncOperation
* ContractAsyncOperation


## Strategy

We provide basic implementation of strategy.
* ChannelConfigurationStrategy
* NettyConnectStrategy
* OkHttpConnectStrategy
* RetryStrategy
* SimpleTimeoutStrategy
* ZipkinTracingStrategy
