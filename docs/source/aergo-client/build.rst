Build
=====

You can build aergo client with configurations. A configuration for a same purpose will be overridden.

.. code-block:: java

  AergoClient aergoClient = new AergoClientBuilder()
      .withEndpoint("localhost:7845")
      .withNonBlockingConnect()  // ignored
      .withBlockingConnect()     // applied
      .build();

Endpoint
--------

You can configure aergo node endpoint to connect. Default is localhost:7845.

.. code-block:: java

  // connect to 'localhost:7845'
  AergoClient aergoClient = new AergoClientBuilder()
      .withEndpoint("localhost:7845")
      .build();

Connect Strategy
----------------

You can configure a strategy to connect.

Non-Blocking connection uses netty internally.

.. code-block:: java

  // connect to 'localhost:7845' with non-blocking connect
  AergoClient aergoClient = new AergoClientBuilder()
      .withEndpoint("localhost:7845")
      .withNonBlockingConnect()
      .build();

Blocking connection uses okhttp internally.

.. code-block:: java

  // connect to 'localhost:7845' with blocking connect
  AergoClient aergoClient = new AergoClientBuilder()
      .withEndpoint("localhost:7845")
      .withBlockingConnect()
      .build();

Connect Type
------------

Connect with plaintext. This is default behavior.

.. code-block:: java

  // connect with plain text
  AergoClient aergoClient = new AergoClientBuilder()
      .withEndpoint("localhost:7845")
      .withPlainText()
      .build();

Connect with tls. Note that client key must be PKCS8 format.

.. code-block:: java

  // prepare cert files
  InputStream serverCert = loadResourceAsStream("/cert/server.crt");
  InputStream clientCert = loadResourceAsStream("/cert/client.crt");
  InputStream clientKey = loadResourceAsStream("/cert/client.pem"); // must be pkcs8 format

  // connect with plain text
  AergoClient aergoClient = new AergoClientBuilder()
      .withEndpoint("localhost:7845")
      .withTransportSecurity("servername", serverCert, clientCert, clientKey)
      .build();

Retry
-----

You can configure retry count on any kind of failure. It just retry the same request with an interval.

.. code-block:: java

  // retry 3 count with a 1000ms interval
  AergoClient aergoClient = new AergoClientBuilder()
      .withRetry(3, 1000L, TimeUnit.MILLISECONDS)
      .build();

Timeout
-------

You can configure timeout without any response for each request.

.. code-block:: java

  // set timeout as 5000ms for each request.
  AergoClient aergoClient = new AergoClientBuilder()
      .withTimeout(5000L, TimeUnit.MILLISECONDS)
      .build();

Close
-----

Close an aergo client. You have to close it to prevent memory leak.

You can close aergo client by calling close method.

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

Since java 7, you can use try-with-resources block to close aergo client.

.. code-block:: java

  // try-with-resources block
  try (AergoClient aergoClient = new AergoClientBuilder()
      .withEndpoint("localhost:7845")
      .build()) {

    // ... do some operations
  }