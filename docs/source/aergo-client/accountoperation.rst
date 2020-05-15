AccountOperation
================

Provides account related operations.

Get Account State
-----------------

Get state of account.

.. code-block:: java

  AccountAddress accountAddress = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  AccountState accountState = client.getAccountOperation().getState(accountAddress);
  System.out.println("AccountState: " + accountState);

Create Name
-----------

Create name which owns by a transaction signer.

.. code-block:: java

  // prepare a signer
  AergoKey signer = richKey;

  // make a naming transaction
  Name name = randomName();
  long nonce = nonceProvider.incrementAndGetNonce(signer.getAddress());
  TxHash txHash = client.getAccountOperation().createNameTx(signer, name, nonce);
  System.out.println("Create name tx hash: " + txHash);

Update Name
-----------

Update name owner to new account. It should be done by origin name owner.

.. code-block:: java

  // prepare a signer
  AergoKey signer = richKey;

  // create an name
  Name name = randomName();
  long nonce1 = nonceProvider.incrementAndGetNonce(signer.getAddress());
  client.getAccountOperation().createNameTx(signer, name, nonce1);

  // sleep
  Thread.sleep(2000L);

  // update an name
  AccountAddress nextOwner = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  long nonce2 = nonceProvider.incrementAndGetNonce(signer.getAddress());
  TxHash txHash = client.getAccountOperation().updateNameTx(signer, name, nextOwner, nonce2);
  System.out.println("Update name tx hash: " + txHash);

Get Name Owner
--------------

Get name owner.

At current block.

.. code-block:: java

  // get name owner at current block
  Name name = Name.of("samplename11");
  AccountAddress nameOwner = client.getAccountOperation().getNameOwner(name);
  System.out.println("Nonce owner: " + nameOwner);

.. code-block:: java

  // get name owner at block 3
  Name name = Name.of("samplename11");
  AccountAddress nameOwner = client.getAccountOperation().getNameOwner(name, 3);
  System.out.println("Nonce owner: " + nameOwner);

Stake
-----

Stake an aergo.

.. code-block:: java

  // prepare a signer
  AergoKey signer = richKey;

  // stake 10000 aergo
  Aer amount = Aer.of("10000", Unit.AERGO);
  long nonce = nonceProvider.incrementAndGetNonce(signer.getAddress());
  TxHash txHash = client.getAccountOperation().stakeTx(signer, amount, nonce);
  System.out.println("Stake tx hash: " + txHash);

Unstake
-------

UnStake an aergo.

.. code-block:: java

  // prepare a signer
  AergoKey signer = richKey;

  // unstake 10000 aergo
  Aer amount = Aer.of("10000", Unit.AERGO);
  long nonce = nonceProvider.incrementAndGetNonce(signer.getAddress());
  TxHash txHash = client.getAccountOperation().unstakeTx(signer, amount, nonce);
  System.out.println("Unstake tx hash: " + txHash);

Get Stake Info
--------------

Get stake info of an account.

.. code-block:: java

  AccountAddress accountAddress = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  StakeInfo stakeInfo = client.getAccountOperation().getStakingInfo(accountAddress);
  System.out.println("Stake info: " + stakeInfo);

Vote
----

Vote candidate to a vote id.

.. code-block:: java

  // prepare a signer
  AergoKey signer = richKey;

  // vote to "voteBP"
  List<String> candidates = asList("16Uiu2HAkwWbv8nKx7S6S5NMvUpTLNeXMVCPr3NTnrx6rBPYYiQ4K");
  long nonce = nonceProvider.incrementAndGetNonce(signer.getAddress());
  TxHash txHash = client.getAccountOperation().voteTx(signer, "voteBp", candidates, nonce);
  System.out.println("Vote tx hash: " + txHash);

Get Vote of Account
-------------------

Get vote info of an account.

.. code-block:: java

  AccountAddress accountAddress = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  AccountTotalVote voteInfo = client.getAccountOperation().getVotesOf(accountAddress);
  System.out.println("Vote info: " + voteInfo);

Get Vote Result
---------------

Get vote result for vote id.

.. code-block:: java

  // get vote result for vote id "voteBP" for top 23 candidates.
  List<ElectedCandidate> elected = client.getAccountOperation().listElected("voteBP", 23);
  System.out.println("Elected: " + elected);
