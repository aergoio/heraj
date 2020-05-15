NonceProvider
=============

NonceProvider is an interface for providing nonce to be used in making transaction. Heraj provides SimpleNonceProvider. It's thread-safe and has capacity to prevent memory leak. It remove least recently used value on adding new nonce value on full capacity.

Create
------

Create a SimpleNonceProvider.

With explicit capacity.

.. code-block:: java

  // create nonce provider with capacity 100
  NonceProvider nonceProvider = new SimpleNonceProvider(100);

With implicit capacity.

.. code-block:: java

  // create nonce provider with capacity 1000
  NonceProvider nonceProvider = new SimpleNonceProvider();

Bind
----

Bind nonce for an address. If capacity is full, least recently used address will be removed.

For address.

.. code-block:: java

  AccountAddress accountAddress = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  NonceProvider nonceProvider = new SimpleNonceProvider();
  nonceProvider.bindNonce(accountAddress, 30L);
  System.out.println("Binded nonce: " + nonceProvider.getLastUsedNonce(accountAddress));

Using account state. It binds nonce for corresponding state.

.. code-block:: java

  AccountAddress accountAddress = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  AccountState accountState = client.getAccountOperation().getState(accountAddress);
  NonceProvider nonceProvider = new SimpleNonceProvider();
  System.out.println("Binded nonce: " + nonceProvider.getLastUsedNonce(accountAddress));

Use
---

Increment and get nonce. It's thread-safe.

.. code-block:: java

  AergoKey signer = richKey;
  NonceProvider nonceProvider = new SimpleNonceProvider();
  long nonce = nonceProvider.incrementAndGetNonce(signer.getAddress());
  System.out.println("Next nonce: " + nonce);

Get last used nonce.

.. code-block:: java

  AergoKey signer = richKey;
  NonceProvider nonceProvider = new SimpleNonceProvider();
  long nonce = nonceProvider.getLastUsedNonce(signer.getAddress());
  System.out.println("Last used nonce: " + nonce);

Example
-------

.. code-block:: java

  // prepare signer
  AergoKey signer = richKey;

  // create an nonce provider
  AccountState accountState = client.getAccountOperation().getState(signer.getAddress());
  NonceProvider nonceProvider = new SimpleNonceProvider();
  nonceProvider.bindNonce(accountState);

  // print current
  long currentNonce = nonceProvider.getLastUsedNonce(signer.getAddress());
  System.out.println("Current nonce: " + currentNonce);

  // request using thread pool
  AccountAddress accountAddress = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  ExecutorService service = Executors.newCachedThreadPool();
  IntStream.range(0, 1000).forEach(i -> {
    service.submit(() -> {
      // get nonce to use
      long nonce = nonceProvider.incrementAndGetNonce(signer.getAddress());
      client.getTransactionOperation().sendTx(signer, accountAddress, Aer.ONE, nonce,
          Fee.INFINITY, BytesValue.EMPTY);
    });
  });

  // stop the service
  service.awaitTermination(3000L, TimeUnit.MILLISECONDS);
  service.shutdown();

  // should print 1000
  long lastUsedNonce = nonceProvider.getLastUsedNonce(signer.getAddress());
  System.out.println("Nonce difference: " + (lastUsedNonce - currentNonce));
