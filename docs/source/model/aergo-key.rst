Aergo Key
=========

AergoKey is a core of account. It holds private key for an address. It can also sign transaction and message.

New
---

.. code-block:: java

  AergoKey aergoKey = new AergoKeyGenerator().create();
  System.out.println(aergoKey);

Export
------

Wallet Import Format
^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

  EncryptedPrivateKey wif = aergoKey.exportAsWif("password");
  System.out.println(wif);

KeyStore Format
^^^^^^^^^^^^^^^

.. code-block:: java

  TODO

Import
------

Wallet Import Format
^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

  EncryptedPrivateKey importedWif = EncryptedPrivateKey
      .of("47btMyQmmWddJmEigUp8HjUPam94Jjtf6eG6SW74r61YmbcJGyoxhwTBa8XhVBQ9wYm468DED");
  AergoKey imported = AergoKey.of(importedWif, "password");
  System.out.println(imported);

KeyStore Format
^^^^^^^^^^^^^^^

.. code-block:: java

  TODO

Sign
----

Transaction
^^^^^^^^^^^

.. code-block:: java

  RawTransaction rawTransaction = RawTransaction.newBuilder(ChainIdHash.of(BytesValue.EMPTY))
      .from(aergoKey.getAddress())
      .to(aergoKey.getAddress())
      .amount(Aer.AERGO_ONE)
      .nonce(1L)
      .build();
  Transaction transaction = aergoKey.sign(rawTransaction);
  System.out.println(transaction);

Message
^^^^^^^

.. code-block:: java

  Signature signature = aergoKey.signMessage(BytesValue.of("I'm message".getBytes()));
  System.out.println(signature);

Verify
------

Transaction
^^^^^^^^^^^

.. code-block:: java

  Transaction transaction = ...;
  Verifier verifier = new AergoSignVerifier();
  boolean verifyTx = verifier.verify(transaction);

Mesasge
^^^^^^^

.. code-block:: java

  TODO