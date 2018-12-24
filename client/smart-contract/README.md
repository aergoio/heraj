# Smart contract

Smart contract module provides an easy way to executing, querying smart contract with user-defined java interface.

## Kind of object

### SmartContract

An basic interface to execute smart contract using java interface. User-define interface must extends this interface.

### ContractInvocationHandler

The secret of this module is dynamic proxy. _ContractInvocationHandler_ is an invocation handler for dynamic proxy which is created by _SmartContractFactory_.
