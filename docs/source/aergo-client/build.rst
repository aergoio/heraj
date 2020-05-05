Build
=====

You can build aergo client with configurations. A configuration for a same purpose is overridden. That is if you configure for connect strategy like

.. code-block:: java

  AergoClient aergoClient = new AergoClientBuilder()
      .withEndpoint("localhost:7845")
      .withNonBlockingConnect()
      .withBlockingConnect()
      .build();

then last one is used (blocking connect in this case).

Endpoint
--------

You can configure rpc endpoint for aergo. Default is localhost:7845.

.. code-block:: java

  AergoClient endpointClient = new AergoClientBuilder()
      .withEndpoint("localhost:7845")
      .build();

Connect Strategy
----------------

Non-Blocking Connect
^^^^^^^^^^^^^^^^^^^^

It uses `Netty`_ internally. This is default strategy.

.. code-block:: java

  AergoClient nettyClient = new AergoClientBuilder()
      .withEndpoint("localhost:7845")
      .withNonBlockingConnect()
      .build();

Blocking Connect
^^^^^^^^^^^^^^^^

It uses `OkHttp`_ internally. Recommanded for android usage.

.. code-block:: java

  AergoClient okhttpClient = new AergoClientBuilder()
      .withEndpoint("localhost:7845")
      .withBlockingConnect()
      .build();

Connect Type
------------

Plain Text
^^^^^^^^^^

You can connect with plaintext. This is default behavior.

.. code-block:: java

  AergoClient plainTextClient = new AergoClientBuilder()
      .withEndpoint("localhost:7845")
      .withPlainText()
      .build();

TLS
^^^

You can connect with tls. Note that client key must be `PKCS8`_ format.

.. code-block:: java

  AergoClient tlsClient = new AergoClientBuilder()
      .withEndpoint("localhost:7845")
      .withTransportSecurity("servername", "${path_to_server_cert}", "${path_to_client_cert}", "${path_to_client_key}")
      .build();

Retry
-----

It just retry with a same request on any kind of failure. Default is off.

.. code-block:: java

  AergoClient retryClient = new AergoClientBuilder()
      .withRetry(3, 1000L, TimeUnit.MILLISECONDS)
      .build();

Timeout
-------

It enable request timeout. Default is 3 seconds.

.. code-block:: java

  AergoClient timeoutClient = new AergoClientBuilder()
      .withTimeout(5000L, TimeUnit.MILLISECONDS)
      .build();

Close
-----

You have to close the client to prevent memory leak.

With close method.

.. code-block:: java

  // create
  AergoClient aergoClient = new AergoClientBuilder()
      .withEndpoint("localhost:7845")
      .withBlockingConnect()
      .withTimeout(10000L, TimeUnit.MILLISECONDS)
      .build();

  // ... do some operations

  // close
  aergoClient.close();

With jdk7 try-with-resources pattern.

.. code-block:: java

  // autoclose
  try (AergoClient autoClose = new AergoClientBuilder()
      .withEndpoint("localhost:7845")
      .withBlockingConnect()
      .withTimeout(10000L, TimeUnit.MILLISECONDS)
      .build()) {

    // ... do some operations
  }



.. _Netty: https://netty.io/
.. _OkHttp: https://square.github.io/okhttp/
.. _PKCS8: https://en.wikipedia.org/wiki/PKCS_8
