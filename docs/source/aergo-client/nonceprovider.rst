NonceProvider
=============

NonceProvider is an interface for providing nonce to be used in making transaction.

Create
------

Heraj provides **SimpleNonceProvider** holding nonce for each addresses. It is thread-safe.
It has capacity to prevent memory leak. If capacity is full, adding nonce for an new address will remove nonce for least recently used address. Default capacity is 1000.
You may your own nonce provider if necessary.

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