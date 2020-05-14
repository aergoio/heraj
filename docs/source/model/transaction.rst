Transaction
===========

Transaction is a atomic unit of blockchain. Almost all operation is done on transaction.

Make a transaction
------------------

Heraj provides dsl for making aergo transaction.

Plain Transaction
^^^^^^^^^^^^^^^^^

.. code-block:: java

  // make a plain transaction
  AergoKey aergoKey = new AergoKeyGenerator().create();
  ChainIdHash chainIdHash = ChainIdHash.of("6YCMGJu3UN66ULzUuS5R7GTxXLDsSjRdjWPB94EiqMJc");
  RawTransaction rawTransaction = RawTransaction.newBuilder()
      .chainIdHash(chainIdHash)
      .from(aergoKey.getAddress())
      .to(aergoKey.getAddress())
      .amount(Aer.AERGO_ONE)
      .nonce(1L)
      .fee(Fee.ZERO)
      .payload(BytesValue.of("contract_payload".getBytes()))
      .build();
  Transaction transaction = aergoKey.sign(rawTransaction);
  System.out.println("Plain transaction: " + transaction);

Deploy Contract Transaction
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

  // make a contract definition
  ContractDefinition definition = ContractDefinition.newBuilder()
      .encodedContract(
          "FppTEQaroys1N4P8RcAYYiEhHaQaRE9fzANUx4q2RHDXaRo6TYiTa61n25JcV19grEhpg8qdCWVdsDE2yVfuTKxxcdsTQA2B5zTfxA4GqeRqYGYgWJpj1geuLJAn1RjotdRRxSS1BFA6CAftxjcgiP6WUHacmgtNzoWViYESykhjqVLdmTfV12d44wfh9YAgQ57aRkLNCPkujbnJhdhHEtY1hrJYLCxUDBveqVcDhrrvcHtjDAUcZ5UMzbg6qR1kthGB1Lua6ymw1BmfySNtqb1b6Hp92UPMa7gi5FpAXF5XgpQtEbYDXMbtgu5XtXNhNejrtArcekmjrmPXRoTnMDGUQFcALtnNCrgSv2z5PiXP1coGEbHLTTbxkmJmJz6arEfsb6J1Dv7wnvgysDFVApcpABfwMjHLmnEGvUCLthRfHNBDGydx9jvJQvismqdpDfcEaNBCo5SRMCqGS1FtKtpXjRaHGGFGcTfo9axnsJgAGxLk")
      .amount(Aer.ZERO)
      .constructorArgs(1, 2)
      .build();

  // make a contract deployment transaction
  AergoKey aergoKey = new AergoKeyGenerator().create();
  ChainIdHash chainIdHash = ChainIdHash.of("6YCMGJu3UN66ULzUuS5R7GTxXLDsSjRdjWPB94EiqMJc");
  RawTransaction rawTransaction = RawTransaction.newDeployContractBuilder()
      .chainIdHash(chainIdHash)
      .from(aergoKey.getAddress())
      .definition(definition)
      .nonce(1L)
      .fee(Fee.ZERO)
      .build();
  Transaction transaction = aergoKey.sign(rawTransaction);
  System.out.println("Contract deployment transaction: " + transaction);

Invoke Contract Transaction
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

  // make a contract invocation
  ContractInterface contractInterface = dummyContractInterface();
  ContractInvocation invocation = contractInterface.newInvocationBuilder()
      .function("set")
      .args("key", "123")
      .delegateFee(false)
      .build();

  // make a contract invocation transaction
  AergoKey aergoKey = new AergoKeyGenerator().create();
  ChainIdHash chainIdHash = ChainIdHash.of("6YCMGJu3UN66ULzUuS5R7GTxXLDsSjRdjWPB94EiqMJc");
  RawTransaction rawTransaction = RawTransaction.newInvokeContractBuilder()
      .chainIdHash(chainIdHash)
      .from(aergoKey.getAddress())
      .invocation(invocation)
      .nonce(1L)
      .fee(Fee.ZERO)
      .build();
  Transaction transaction = aergoKey.sign(rawTransaction);
  System.out.println("Invoke contract transaction: " + transaction);

Redeploy Contract
^^^^^^^^^^^^^^^^^

.. code-block:: java

  // make an new contract definition
  ContractDefinition reDeployTarget = ContractDefinition.newBuilder()
      .encodedContract(
          "FppTEQaroys1N4P8RcAYYiEhHaQaRE9fzANUx4q2RHDXaRo6TYiTa61n25JcV19grEhpg8qdCWVdsDE2yVfuTKxxcdsTQA2B5zTfxA4GqeRqYGYgWJpj1geuLJAn1RjotdRRxSS1BFA6CAftxjcgiP6WUHacmgtNzoWViYESykhjqVLdmTfV12d44wfh9YAgQ57aRkLNCPkujbnJhdhHEtY1hrJYLCxUDBveqVcDhrrvcHtjDAUcZ5UMzbg6qR1kthGB1Lua6ymw1BmfySNtqb1b6Hp92UPMa7gi5FpAXF5XgpQtEbYDXMbtgu5XtXNhNejrtArcekmjrmPXRoTnMDGUQFcALtnNCrgSv2z5PiXP1coGEbHLTTbxkmJmJz6arEfsb6J1Dv7wnvgysDFVApcpABfwMjHLmnEGvUCLthRfHNBDGydx9jvJQvismqdpDfcEaNBCo5SRMCqGS1FtKtpXjRaHGGFGcTfo9axnsJgAGxLk")
      .amount(Aer.ZERO)
      .constructorArgs(1, 2)
      .build();

  // make a contract redeployment transaction
  AergoKey aergoKey = new AergoKeyGenerator().create();
  ChainIdHash chainIdHash = ChainIdHash.of("6YCMGJu3UN66ULzUuS5R7GTxXLDsSjRdjWPB94EiqMJc");
  RawTransaction rawTransaction = RawTransaction.newReDeployContractBuilder()
      .chainIdHash(chainIdHash)
      .creator(aergoKey.getAddress()) // must be creator
      .contractAddress(
          ContractAddress.of("AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd"))
      .definition(reDeployTarget)
      .nonce(1L)
      .fee(Fee.ZERO)
      .build();
  Transaction transaction = aergoKey.sign(rawTransaction);
  System.out.println("Contarct redeployment transaction: " + transaction);

Create Name
^^^^^^^^^^^

.. code-block:: java

  // make an name creation transaction
  AergoKey aergoKey = new AergoKeyGenerator().create();
  ChainIdHash chainIdHash = ChainIdHash.of("6YCMGJu3UN66ULzUuS5R7GTxXLDsSjRdjWPB94EiqMJc");
  RawTransaction rawTransaction = RawTransaction.newCreateNameTxBuilder()
      .chainIdHash(chainIdHash)
      .from(aergoKey.getAddress())
      .name(Name.of("namenamename"))
      .nonce(1L)
      .build();
  Transaction transaction = aergoKey.sign(rawTransaction);
  System.out.println("Create name transaction: " + transaction);

Update Name
^^^^^^^^^^^

.. code-block:: java

  // make an name update transaction
  AergoKey aergoKey = new AergoKeyGenerator().create();
  ChainIdHash chainIdHash = ChainIdHash.of("6YCMGJu3UN66ULzUuS5R7GTxXLDsSjRdjWPB94EiqMJc");
  RawTransaction rawTransaction = RawTransaction.newUpdateNameTxBuilder()
      .chainIdHash(chainIdHash)
      .from(aergoKey.getAddress())
      .name("namenamename")
      .nextOwner(AccountAddress.of("AmgVbUZiReUVFXdYb4UVMru4ZqyicSsFPqumRx8LfwMKLFk66SNw"))
      .nonce(1L)
      .build();
  Transaction transaction = aergoKey.sign(rawTransaction);
  System.out.println("Update name transaction: " + transaction);

Stake
^^^^^

.. code-block:: java

  // make a stake transaction
  AergoKey aergoKey = new AergoKeyGenerator().create();
  ChainIdHash chainIdHash = ChainIdHash.of("6YCMGJu3UN66ULzUuS5R7GTxXLDsSjRdjWPB94EiqMJc");
  RawTransaction rawTransaction = RawTransaction.newStakeTxBuilder()
      .chainIdHash(chainIdHash)
      .from(aergoKey.getAddress())
      .amount(Aer.of("10000", Unit.AERGO))
      .nonce(1L)
      .build();
  Transaction transaction = aergoKey.sign(rawTransaction);
  System.out.println("Stake transaction: " + transaction);

Unstake
^^^^^^^

.. code-block:: java

  // make a unstake transaction
  AergoKey aergoKey = new AergoKeyGenerator().create();
  ChainIdHash chainIdHash = ChainIdHash.of("6YCMGJu3UN66ULzUuS5R7GTxXLDsSjRdjWPB94EiqMJc");
  RawTransaction rawTransaction = RawTransaction.newUnstakeTxBuilder()
      .chainIdHash(chainIdHash)
      .from(aergoKey.getAddress())
      .amount(Aer.of("10000", Unit.AERGO))
      .nonce(1L)
      .build();
  Transaction transaction = aergoKey.sign(rawTransaction);
  System.out.println("Unstake transaction: " + transaction);

Vote
^^^^

.. code-block:: java

  // make a vote transaction
  AergoKey aergoKey = new AergoKeyGenerator().create();
  ChainIdHash chainIdHash = ChainIdHash.of("6YCMGJu3UN66ULzUuS5R7GTxXLDsSjRdjWPB94EiqMJc");
  RawTransaction rawTransaction = RawTransaction.newVoteTxBuilder()
      .chainIdHash(chainIdHash)
      .from(aergoKey.getAddress())
      .voteId("voteBP")
      .candidates(asList("123", "456"))
      .nonce(1L)
      .build();
  Transaction transaction = aergoKey.sign(rawTransaction);
  System.out.println("Vote transaction: " + transaction);

Parse Payload to Model
----------------------

Heraj also provides utilis for parsing payload to heraj model. Currnetly ContractInvocation is supported only.

Contract Invocation
^^^^^^^^^^^^^^^^^^^

.. code-block:: java

  // make a contract invocation
  ContractInterface contractInterface = dummyContractInterface();
  ContractInvocation invocation = contractInterface.newInvocationBuilder()
      .function("set")
      .args("key", "123")
      .delegateFee(false)
      .build();

  // make a contract invocation transaction
  AergoKey aergoKey = new AergoKeyGenerator().create();
  ChainIdHash chainIdHash = ChainIdHash.of("6YCMGJu3UN66ULzUuS5R7GTxXLDsSjRdjWPB94EiqMJc");
  RawTransaction rawTransaction = RawTransaction.newInvokeContractBuilder()
      .chainIdHash(chainIdHash)
      .from(aergoKey.getAddress())
      .invocation(invocation)
      .nonce(1L)
      .fee(Fee.ZERO)
      .build();

  // parse contract invocation info
  PayloadConverter<ContractInvocation> invocationConverter =
      new ContractInvocationPayloadConverter();
  ContractInvocation parsedInvocation = invocationConverter
      .parseToModel(rawTransaction.getPayload());
  System.out.println("Parsed contract invocation: " + parsedInvocation.getAddress());