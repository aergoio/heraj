TrasactionOperation
===================

Provides transaction related operations.

Make a transaction
------------------

We provide step-builder pattern for transactions.

PlainTransaction
^^^^^^^^^^^^^^^^

.. code-block:: java

  AergoKey aergoKey = new AergoKeyGenerator().create();
  RawTransaction plainTransaction = RawTransaction.newBuilder()
      .chainIdHash(ChainIdHash.of(BytesValue.EMPTY))
      .from(aergoKey.getAddress())
      .to(aergoKey.getAddress())
      .amount(Aer.AERGO_ONE)
      .nonce(1L)
      .fee(Fee.ZERO)
      .payload(BytesValue.of("payload".getBytes()))
      .build();

DeployContractTransaction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

  TODO

InvokeContractTransaction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

  TODO

ReDeployContractTransaction
^^^^^^^^^^^^^^^^^^^^^^^^^^^

It works on private mode only.

.. code-block:: java

  TODO

CreateNameTransaction
^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

  TODO

UpdateNameTransaction
^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

  TODO

StakeTransaction
^^^^^^^^^^^^^^^^

.. code-block:: java

  TODO

UnStakeTransaction
^^^^^^^^^^^^^^^^^^

.. code-block:: java

  TODO

VoteTransaction
^^^^^^^^^^^^^^^

.. code-block:: java

  TODO

Commit Transaction
------------------

.. code-block:: java

  AergoKey aergoKey = new AergoKeyGenerator().create();
  RawTransaction rawTransaction = RawTransaction.newBuilder()
      .chainIdHash(ChainIdHash.of(BytesValue.EMPTY))
      .from(aergoKey.getAddress())
      .to(aergoKey.getAddress())
      .amount(Aer.AERGO_ONE)
      .nonce(1L)
      .fee(Fee.ZERO)
      .payload(BytesValue.of("payload".getBytes()))
      .build();
  Transaction signed = aergoKey.sign(rawTransaction);
  TxHash commited = client.getTransactionOperation().commit(signed);

Get transaction Info
--------------------

Get transaction by hash. It returns transaction in both mempool and state db.

.. code-block:: java

  TxHash txHash = TxHash.of("EGXNDgjY2vQ6uuP3UF3dNXud54dF4FNVY181kaeQ26H9");
  Transaction getResult = client.getTransactionOperation().getTransaction(txHash);

Get Payload Info
----------------

You can parse specific payload information from transaction.

ContractDefinitionPayload
^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

  TODO

ContractInvocationPayload
^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

  PayloadConverter<ContractInvocation> invocationConverter = new ContractInvocationPayloadConverter();
  BytesValue payload = ...;
  ContractInvocation parsedInvocation = invocationConverter.parseToModel(payload);

CreateNamePayload
^^^^^^^^^^^^^^^^^

.. code-block:: java

  TODO

UpdateNamePayload
^^^^^^^^^^^^^^^^^

.. code-block:: java

  TODO

StakePayload
^^^^^^^^^^^^

.. code-block:: java

  TODO

UnStakePayload
^^^^^^^^^^^^^^

.. code-block:: java

  TODO

VotePayload
^^^^^^^^^^^

.. code-block:: java

  TODO