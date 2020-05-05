AccountOperation
================

Provides account related operations.

Get Account State
-----------------

.. code-block:: java

  AccountAddress addressToGet = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  AccountState state = client.getAccountOperation().getState(addressToGet);

Name
----

Create Name
^^^^^^^^^^^

.. code-block:: java

  AergoKey key = new AergoKeyGenerator().create();
  TxHash createNameTxHash = client.getAccountOperation()
      .createName(key, "testtesttest", 1L);

Update Name
^^^^^^^^^^^

.. code-block:: java

  AergoKey key = new AergoKeyGenerator().create();
  AccountAddress newOwner = new AergoKeyGenerator().create().getAddress();
  TxHash updateNameTxHash = client.getAccountOperation()
      .updateName(key, "testtesttest", newOwner, 2L);

Get Name Owner
^^^^^^^^^^^^^^

.. code-block:: java

  AccountAddress nameOwner = client.getAccountOperation().getNameOwner("testtesttest");

Staking
-------

Stake
^^^^^

.. code-block:: java

  AergoKey key = new AergoKeyGenerator().create();
  TxHash stakeTxHash = client.getAccountOperation().stake(key, Aer.of("10000", Unit.AERGO), 3L);

Unstake
^^^^^^^

.. code-block:: java

  AergoKey key = new AergoKeyGenerator().create();
  TxHash unStakeTxHash = client.getAccountOperation()
      .unstake(key, Aer.of("10000", Unit.AERGO), 4L);

Get Stake Info
^^^^^^^^^^^^^^

.. code-block:: java

  AccountAddress addressToGetStakeInfo = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  StakeInfo stakeInfo = client.getAccountOperation().getStakingInfo(addressToGetStakeInfo);

Voting
------

Vote
^^^^

.. code-block:: java

  // vote to "voteBP" with value "test"
  AergoKey key = new AergoKeyGenerator().create();
  client.getAccountOperation().vote(key, "voteBP", asList("test"), 5L);

Get Vote of Account
^^^^^^^^^^^^^^^^^^^

.. code-block:: java

  AccountAddress addressToGetVoteInfo = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  AccountTotalVote voteInfo = client.getAccountOperation().getVotesOf(addressToGetVoteInfo);

Get Vote Result
^^^^^^^^^^^^^^^

.. code-block:: java

  // get vote info of "voteBP"
  List<ElectedCandidate> elected = client.getAccountOperation()
      .listElected("voteBP", 23);
