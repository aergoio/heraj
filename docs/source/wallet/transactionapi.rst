Transaction Api
===============

TransactionApi provides high-level api for making transaction. It uses unlocked account when making transaction.

Create Name
-----------

Create name with unlocked one.

.. code-block:: java

  // unlock specific account with authentication
  walletApi.unlock(authentication);

  Name name = randomName();
  TxHash txHash = walletApi.with(client).transaction().createName(name);
  System.out.println("Create name tx hash: " + txHash);

  // lock an account
  walletApi.lock();

Update Name
-----------

Update name with unlocked one.

.. code-block:: java

  // unlock specific account with authentication
  walletApi.unlock(authentication);

  // create an name
  Name name = randomName();
  walletApi.with(client).transaction().createName(name);

  // sleep
  Thread.sleep(2000L);

  // update an name
  AccountAddress nextOwner = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  TxHash txHash = walletApi.with(client).transaction().updateName(name, nextOwner);
  System.out.println("Update name tx hash: " + txHash);

  // lock an account
  walletApi.lock();

Stake
-----

Stake with unlocked one.

.. code-block:: java

  // unlock specific account with authentication
  walletApi.unlock(authentication);

  // stake
  TxHash txHash = walletApi.with(client).transaction().stake(Aer.of("10000", Unit.AERGO));
  System.out.println("Stake tx hash: " + txHash);

  // lock an account
  walletApi.lock();

Unstake
-------

Unstake with unlocked one.

.. code-block:: java

  // unlock specific account with authentication
  walletApi.unlock(authentication);

  // unstake
  TxHash txHash = walletApi.with(client).transaction().unstake(Aer.of("10000", Unit.AERGO));
  System.out.println("Unstake tx hash: " + txHash);

  // lock an account
  walletApi.lock();

Vote
----

Vote with unlocked one.

.. code-block:: java

  // unlock specific account with authentication
  walletApi.unlock(authentication);

  // vote to "voteBP"
  List<String> candidates = asList("16Uiu2HAkwWbv8nKx7S6S5NMvUpTLNeXMVCPr3NTnrx6rBPYYiQ4K");
  TxHash txHash = walletApi.with(client).transaction().vote("voteBp", candidates);
  System.out.println("Vote tx hash: " + txHash);

  // lock an account
  walletApi.lock();

Send
----

Send aergo with unlocked one.

Send without payload to address.

.. code-block:: java

  // unlock specific account with authentication
  walletApi.unlock(authentication);

  // send
  AccountAddress accountAddress = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  TxHash txHash = walletApi.with(client).transaction()
      .send(accountAddress, Aer.AERGO_ONE, Fee.INFINITY);
  System.out.println("Send tx hash: " + txHash);

  // lock an account
  walletApi.lock();

Send with payload to address.

.. code-block:: java

  // unlock specific account with authentication
  walletApi.unlock(authentication);

  // send
  AccountAddress accountAddress = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  BytesValue payload = BytesValue.of("test".getBytes());
  TxHash txHash = walletApi.with(client).transaction()
      .send(accountAddress, Aer.AERGO_ONE, Fee.INFINITY, payload);
  System.out.println("Send tx hash: " + txHash);

  // lock an account
  walletApi.lock();

Send without payload to name.

.. code-block:: java

  // unlock specific account with authentication
  walletApi.unlock(authentication);

  // create an name
  Name name = randomName();
  walletApi.with(client).transaction().createName(name);

  // sleep
  Thread.sleep(2000L);

  // send
  TxHash txHash = walletApi.with(client).transaction()
      .send(name, Aer.AERGO_ONE, Fee.INFINITY);
  System.out.println("Send tx hash: " + txHash);

  // lock an account
  walletApi.lock();

Send with payload to name.

.. code-block:: java

  // unlock specific account with authentication
  walletApi.unlock(authentication);

  // create an name
  Name name = randomName();
  walletApi.with(client).transaction().createName(name);

  // sleep
  Thread.sleep(2000L);

  // send
  BytesValue payload = BytesValue.of("test".getBytes());
  TxHash txHash = walletApi.with(client).transaction()
      .send(name, Aer.AERGO_ONE, Fee.INFINITY, payload);
  System.out.println("Send tx hash: " + txHash);

  // lock an account
  walletApi.lock();

Commit
------

Sign with unlocked one and commit it.

.. code-block:: java

  // unlock specific account with authentication
  walletApi.unlock(authentication);

  // create a raw transaction
  AccountAddress current = walletApi.getPrincipal();
  ChainIdHash chainIdHash = walletApi.with(client).query().getChainIdHash();
  AccountState currentState = walletApi.with(client).query().getAccountState(current);
  RawTransaction rawTransaction = RawTransaction.newBuilder()
      .chainIdHash(chainIdHash)
      .from(current)
      .to(current)
      .amount(Aer.AERGO_ONE)
      .nonce(currentState.getNonce() + 1L)
      .build();

  // commit
  TxHash txHash = walletApi.with(client).transaction().commit(rawTransaction);
  System.out.println("Commit tx hash: " + txHash);

  // lock an account
  walletApi.lock();

Commit signed transaction.

.. code-block:: java

  // unlock specific account with authentication
  walletApi.unlock(authentication);

  // create a signed transaction
  AccountAddress current = walletApi.getPrincipal();
  ChainIdHash chainIdHash = walletApi.with(client).query().getChainIdHash();
  AccountState currentState = walletApi.with(client).query().getAccountState(current);
  RawTransaction rawTransaction = RawTransaction.newBuilder()
      .chainIdHash(chainIdHash)
      .from(current)
      .to(current)
      .amount(Aer.AERGO_ONE)
      .nonce(currentState.getNonce() + 1L)
      .build();
  Transaction signed = walletApi.sign(rawTransaction);

  // commit
  TxHash txHash = walletApi.with(client).transaction().commit(signed);
  System.out.println("Commit tx hash: " + txHash);

  // lock an account
  walletApi.lock();

Deploy
------

Deploy with unlocked one. For more about making contract definition, see :doc:`ContractDefinition <../model/contractdefinition>`.

.. code-block:: java

  // unlock specific account with authentication
  walletApi.unlock(authentication);

  // make a contract definition
  String encodedContract = contractPayload;
  ContractDefinition contractDefinition = ContractDefinition.newBuilder()
      .encodedContract(encodedContract)
      .build();

  // deploy contract
  TxHash txHash = walletApi.with(client).transaction().deploy(contractDefinition, Fee.INFINITY);
  System.out.println("Deploy tx hash: " + txHash);

  // sleep
  Thread.sleep(2000L);

  // get ContractTxReceipt
  ContractTxReceipt contractTxReceipt = walletApi.with(client).query()
      .getContractTxReceipt(txHash);
  System.out.println("Deployed contract tx receipt: " + contractTxReceipt);

  // get contract interface
  ContractAddress contractAddress = contractTxReceipt.getContractAddress();
  ContractInterface contractInterface = walletApi.with(client).query()
      .getContractInterface(contractAddress);
  System.out.println("Deployed contract interface: " + contractInterface);

  // lock an account
  walletApi.lock();

Re-Deploy
---------

Redeploy with unlocked one. This operations is valid for private node only. For more about making contract definition, see :doc:`ContractDefinition <../model/contractdefinition>`.

.. code-block:: java

  // unlock specific account with authentication
  walletApi.unlock(authentication);

  // made by aergoluac --compiledContract {some_contract}.lua
  String encodedContract = contractPayload;

  // make a contract definition
  ContractDefinition newDefinition = ContractDefinition.newBuilder()
      .encodedContract(encodedContract)
      .build();

  // redeploy
  ContractAddress contractAddress = contractAddressKeep;
  TxHash txHash = walletApi.with(client).transaction()
      .redeploy(contractAddress, newDefinition, Fee.INFINITY);
  System.out.println("Redeploy tx hash: " + txHash);

  // lock an account
  walletApi.lock();

Execute
-------

Deploy with unlocked one. For more about making contract invocation, see :doc:`ContractInvocation <../model/contractinvocation>`.

.. code-block:: java

  // unlock specific account with authentication
  walletApi.unlock(authentication);

  // make a contract invocation
  ContractInterface contractInterface = contractInterfaceKeep;
  ContractInvocation contractInvocation = contractInterface.newInvocationBuilder()
      .function("set")
      .args("key", 333, "test2")
      .build();

  // execute
  TxHash txHash = walletApi.with(client).transaction()
      .execute(contractInvocation, Fee.INFINITY);
  System.out.println("Execute tx hash: " + txHash);

  // lock an account
  walletApi.lock();
