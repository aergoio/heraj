Wallet Api
==========

WalletApi is an interface to interact with :doc:`KeyStore <./keystore>`. It provides unlocking and locking account. It also provides high-level api using aergo client. WalletApi automatically fill nonce for signer. It commit fails by nonce error, it automatically fetch right nonce and retry with it.

Create
------

To create WalletApi, you need KeyStore.

With implicit retry count and interval on nonce failure.

.. code-block:: java

  // create a keystore
  KeyStore keyStore = KeyStores.newInMemoryKeyStore();

  // create a wallet api
  WalletApi walletApi = new WalletApiFactory().create(keyStore);
  System.out.println("WalletApi: " + walletApi);

With explicit retry count and interval on nonce failure.

.. code-block:: java

  // create a keystore
  KeyStore keyStore = KeyStores.newInMemoryKeyStore();

  // create a wallet api with retry count 5 and interval 1s
  TryCountAndInterval tryCountAndInterval = TryCountAndInterval
      .of(5, Time.of(1L, TimeUnit.SECONDS));
  WalletApi walletApi = new WalletApiFactory().create(keyStore, tryCountAndInterval);
  System.out.println("WalletApi: " + walletApi);

Unlock and Lock
---------------

By unlocking account, you can use unlocked account when making transaction.

.. code-block:: java

  // create a keystore
  KeyStore keyStore = KeyStores.newInMemoryKeyStore();

  // store new key to keystore
  AergoKey aergoKey = new AergoKeyGenerator().create();
  Authentication authentication = Authentication.of(aergoKey.getAddress(), "password");
  keyStore.save(authentication, aergoKey);

  // create a wallet api
  WalletApi walletApi = new WalletApiFactory().create(keyStore);

  // unlock account
  boolean unlockResult = walletApi.unlock(authentication);
  System.out.println("Unlock result: " + unlockResult);
  System.out.println("Currently locked one: " + walletApi.getPrincipal());

  // do something..
  Signature signature = walletApi.signMessage(BytesValue.of("test".getBytes()));
  System.out.println("Signature: " + signature);

  // lock account
  boolean lockResult = walletApi.lock();
  System.out.println("Lock result: " + lockResult);

High Level Api
--------------

WalletApi provides high level api for interacting with aergo node. To use TransactionApi, you have to unlock some account. Query api doesn't need unlocked one.

.. code-block:: java

  // prepare client
  AergoClient aergoClient = new AergoClientBuilder().build();

  // create a keystore
  KeyStore keyStore = KeyStores.newInMemoryKeyStore();

  // create a wallet api
  WalletApi walletApi = new WalletApiFactory().create(keyStore);
  System.out.println("WalletApi: " + walletApi);

  // transaction api
  TransactionApi transactionApi = walletApi.with(aergoClient).transaction();
  System.out.println("Transaction Api: " + transactionApi);

  // query api
  QueryApi queryApi = walletApi.with(aergoClient).query();
  System.out.println("Query Api: " + queryApi);
