NonceProvider
=============

NonceProvider is an interface for providing nonce to be used in making transaction. Heraj provides SimpleNonceProvider. It's thread-safe and has capacity to prevent memory leak. It remove least recently used value on adding new nonce value on full capacity.

Create
------

Create a SimpleNonceProvider.

.. code-block:: java

  /* create nonce provider with capacity 100 */
  NonceProvider nonceProvider = new SimpleNonceProvider(100);

Bind nonce
----------

Bind nonce for an address. If capacity is full, least recently used address will be removed.

.. code-block:: java

  AergoKey key = ...;
  nonceProvider.bindNonce(key.getAddress(), 3L);

Get nonce to use
----------------

Get nocne to be used in transaction. It return 'current nonce + 1' and set to it.

.. code-block:: java

  AergoKey key = ...;
  long nonce = nonceProvider.incrementAndGetNonce(key.getAddress());

Get recently used nonce
-----------------------

Get currently binded nonce for an address.

.. code-block:: java

  AergoKey key = ...;
  long lastUsedNonce = nonceProvider.getLastUsedNonce(key.getAddress());