Encoding
========

Heraj provides encoding/decoding for BytesValue. Supported type is

- Hex
- Base58
- Base58 with Checksum
- Base64

Encode
------

To Hex.

.. code-block:: java

  BytesValue bytesValue = BytesValue.of("test".getBytes());
  String encoded = bytesValue.getEncoded(Encoder.Hex);
  System.out.println(encoded)

To Base58.

.. code-block:: java

  BytesValue bytesValue = BytesValue.of("test".getBytes());
  String encoded = bytesValue.getEncoded(Encoder.Base58);
  System.out.println(encoded);

To Base58 with Checksum.

.. code-block:: java

  BytesValue bytesValue = BytesValue.of("test".getBytes());
  String encoded = bytesValue.getEncoded(Encoder.Base58Check);
  System.out.println(encoded);

To Base64.

.. code-block:: java

  BytesValue bytesValue = BytesValue.of("test".getBytes());
  String encoded = bytesValue.getEncoded(Encoder.Base64);
  System.out.println(encoded);

Decode
------

From Hex.

.. code-block:: java

  String encoded = "74657374";
  BytesValue bytesValue = BytesValue.of(encoded, Decoder.Hex);
  System.out.println(bytesValue);

From Base58.

.. code-block:: java

  String encoded = "3yZe7d";
  BytesValue bytesValue = BytesValue.of(encoded, Decoder.Base58);
  System.out.println(bytesValue);

From Base58 with Checksum.

.. code-block:: java

  String encoded = "LUC1eAJa5jW";
  BytesValue bytesValue = BytesValue.of(encoded, Decoder.Base58Check);
  System.out.println(bytesValue);

From Base64.

.. code-block:: java

  String encoded = "dGVzdA==";
  BytesValue bytesValue = BytesValue.of(encoded, Decoder.Base64);
  System.out.println(bytesValue);


Example
-------

Read signature in Base64.

.. code-block:: java

  String encoded = "MEUCIQDP3ywVXX1DP42nTgM6cF95GFfpoEcl4D9ZP+MHO7SgoQIgdq2UiEiSp23lcPFzCHtDmh7pVzsow5x1s8p5Kz0aN7I=";
  BytesValue rawSignature = BytesValue.of(encoded, Decoder.Base64);
  Signature signature = Signature.of(rawSignature);
  System.out.println(signature);