TrasactionOperation
===================

Provides transaction related operations.

Get Transaction
---------------

Get transaction info. It returns null if no corresponding one.

.. code-block:: java

  TxHash txHash = TxHash.of("39vLyMqsg1mTT9mF5NbADgNB2YUiRVsT6SUkDujBZme8");
  Transaction transaction = client.getTransactionOperation().getTransaction(txHash);
  System.out.println("Transaction: " + transaction);

Get Transaction Receipt
-----------------------

Get receipt of transaction. It returns null if no corresponding one.

.. code-block:: java

  TxHash txHash = TxHash.of("39vLyMqsg1mTT9mF5NbADgNB2YUiRVsT6SUkDujBZme8");
  TxReceipt txReceipt = client.getTransactionOperation().getTxReceipt(txHash);
  System.out.println("Transaction receipt: " + txReceipt);

Commit
------

Commit a signed transaction. For more about making transaction, see :doc:`Transaction <../model/transaction>`.

.. code-block:: java

  // get chain id hash
  ChainIdHash chainIdHash = client.getBlockchainOperation().getChainIdHash();

  // prepare signer
  AergoKey signer = richKey;

  // make a transaction
  long nonce = nonceProvider.incrementAndGetNonce(signer.getAddress());
  RawTransaction rawTransaction = RawTransaction.newBuilder()
      .chainIdHash(chainIdHash)
      .from(signer.getAddress())
      .to(signer.getAddress())
      .amount(Aer.AERGO_ONE)
      .nonce(nonce)
      .fee(Fee.ZERO)
      .payload(BytesValue.of("contract_payload".getBytes()))
      .build();

  // sign raw transaction
  Transaction transaction = signer.sign(rawTransaction);

  // commit signed one
  TxHash txHash = client.getTransactionOperation().commit(transaction);
  System.out.println("Commit tx hash: " + txHash);

Send
----

Make a transaction which sends aergo.

By address.

.. code-block:: java

  // prepare signer
  AergoKey signer = richKey;

  // make a send transaction
  AccountAddress accountAddress = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  long nonce = nonceProvider.incrementAndGetNonce(signer.getAddress());
  TxHash txHash = client.getTransactionOperation()
      .sendTx(signer, accountAddress, Aer.ONE, nonce, Fee.INFINITY, BytesValue.EMPTY);
  System.out.println("Send tx hash: " + txHash);

By name.

.. code-block:: java

  // prepare signer
  AergoKey signer = richKey;

  // create an name
  Name name = randomName();
  long nonce1 = nonceProvider.incrementAndGetNonce(signer.getAddress());
  client.getAccountOperation().createNameTx(signer, name, nonce1);

  // sleep
  Thread.sleep(2000L);

  // make a send transaction
  long nonce2 = nonceProvider.incrementAndGetNonce(signer.getAddress());
  TxHash txHash = client.getTransactionOperation()
      .sendTx(signer, name, Aer.ONE, nonce2, Fee.INFINITY, BytesValue.EMPTY);
  System.out.println("Send tx hash: " + txHash);