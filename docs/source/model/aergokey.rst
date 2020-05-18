AergoKey
========

AergoKey is a core of account. It holds private key for an address.

New
---

You can make an new AergoKey using AergoKeyGenerator.

.. code-block:: java

  AergoKey aergoKey = new AergoKeyGenerator().create();
  System.out.println(aergoKey);

Export
------

You can export AergoKey in a different format.

Export as wallet import format.

.. code-block:: java

  AergoKey aergoKey = new AergoKeyGenerator().create();
  EncryptedPrivateKey wif = aergoKey.exportAsWif("password");
  System.out.println("Wallet Import Format: " + wif);

Export as keyformat.

.. code-block:: java

  AergoKey aergoKey = new AergoKeyGenerator().create();
  KeyFormat keyFormat = aergoKey.exportAsKeyFormat("password");
  System.out.println("KeyFormat: " + keyFormat);

Import
------

You can import AergoKey from a different format.

Import with wif.

.. code-block:: java

  EncryptedPrivateKey importedWif = EncryptedPrivateKey
      .of("47btMyQmmWddJmEigUp8HjUPam94Jjtf6eG6SW74r61YmbcJGyoxhwTBa8XhVBQ9wYm468DED");
  AergoKey imported = AergoKey.of(importedWif, "password");
  System.out.println("Imported from wif: " + imported);

Import with keyformat.

.. code-block:: java

  String keystore = loadResource(
      "/AmPo7xZJoKNfZXg4NMt9n2saXpKRSkMXwEzqEAfzbVWC71HQL3hn__keystore.txt");
  KeyFormat keyFormat = KeyFormat.of(BytesValue.of(keystore.getBytes()));
  AergoKey imported = AergoKey.of(keyFormat, "password");
  System.out.println("Imported from keyformat: " + imported);

Sign and Verify
---------------

You can sign transaction and message with an AergoKey. Heraj also provides utils to verify it.

On transaction.

.. code-block:: java

  // prepare aergo key
  AergoKey aergoKey = new AergoKeyGenerator().create();

  // sign transaction
  RawTransaction rawTransaction = RawTransaction.newBuilder(ChainIdHash.of(BytesValue.EMPTY))
      .from(aergoKey.getAddress())
      .to(aergoKey.getAddress())
      .amount(Aer.AERGO_ONE)
      .nonce(1L)
      .build();
  Transaction transaction = aergoKey.sign(rawTransaction);
  System.out.println("Signed transaction: " + transaction);

  // verify transaction
  Verifier verifier = new AergoSignVerifier();
  boolean result = verifier.verify(transaction);
  System.out.println("Verify result: " + result);

On plain message. It hashes plain message and signs it.

.. code-block:: java

  // prepare aergo key
  AergoKey aergoKey = new AergoKeyGenerator().create();

  // sign message
  BytesValue plainMessage = BytesValue.of("test".getBytes());
  Signature signature = aergoKey.signMessage(plainMessage);
  System.out.println("Signature: " + signature);

  // verify signature
  Verifier verifier = new AergoSignVerifier();
  boolean result = verifier.verify(aergoKey.getAddress(), plainMessage, signature);
  System.out.println("Verify result: " + result);

On hashed message. It signs directly without any hashing.

.. code-block:: java

  // prepare aergo key
  AergoKey aergoKey = new AergoKeyGenerator().create();

  // sign sha-256 hashed message
  BytesValue plainMessage = BytesValue.of("test".getBytes());
  MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
  byte[] rawHashed = messageDigest.digest(plainMessage.getValue());
  Hash hashedMessage = Hash.of(BytesValue.of(rawHashed));
  Signature signature = aergoKey.signMessage(hashedMessage);
  System.out.println("Signature: " + signature);

  // verify signature
  Verifier verifier = new AergoSignVerifier();
  boolean result = verifier.verify(aergoKey.getAddress(), hashedMessage, signature);
  System.out.println("Verify result: " + result);