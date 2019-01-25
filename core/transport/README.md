# Transport

Transport module is intended for communication with aergo chain. We use grpc for communication.

## AergoClient

`AergoClient` is a simple client with a context. It has 6 type of operations:

* AccountOperation
* KeyStoreOperation
* BlockOperation
* BlockchainOperation
* TransactionOperation
* ContractOperation

## Strategy

We provide basic implementation of strategy.

* NettyConnectStrategy
* OkHttpConnectStrategy
* RetryStrategy
* SimpleTimeoutStrategy