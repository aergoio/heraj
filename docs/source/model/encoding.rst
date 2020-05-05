Encoding
========

Heraj provides encoding/decoding for bytesvalue. Supported type is

- Hex
- Base58
- Base58 With Checksum
- Base64

Encoding
--------

.. code-block:: java

  // to hex
  String hexEncoded = bytesValue.getEncoded(Encoder.Hex);

  // to base58
  String base58Encoded = bytesValue.getEncoded(Encoder.Base58);

  // to base58 with checksum
  String base58WithCheckEncoded = bytesValue.getEncoded(Encoder.Base58Check);

  // to base64
  String base64Encoded = bytesValue.getEncoded(Encoder.Base64);

Decoding
--------

.. code-block:: java

  // from hex
  BytesValue fromHex = BytesValue
      .of("307864333862306339646363383931666332623735633136643837653063303837373735333031323039323664356361663566323466396634356531636439316639",
          Decoder.Hex);

  // from base58
  BytesValue fromBase58 = BytesValue
      .of("KszNdKzDtTde6mo4ute7nkawftKUGfEhqCcRkCaEVKpPU4iGEJWSScRUrVyhsmNGQ6KFbueikshvtgJqXhjVLZpRxk",
          Decoder.Base58);

  // from base58
  BytesValue fromBase58WithCheck = BytesValue
      .of("38YDwtHjcVvSM56oCVXkGfuTP46QKfXE9E22oYJY5h5YrX6a7rvbuzxf7Y5A5vSpCcab7gsAenQK9rXpNsAWjUm7jKRD86g9",
          Decoder.Base58Check);

  // from base64
  BytesValue fromBase64 = BytesValue
      .of("MHhkMzhiMGM5ZGNjODkxZmMyYjc1YzE2ZDg3ZTBjMDg3Nzc1MzAxMjA5MjZkNWNhZjVmMjRmOWY0NWUxY2Q5MWY5",
          Decoder.Base64);

Usage
-----

Decode signature in base64. Even if default encoding signature is base58, yet signature may be encoded in another format (eg. base64). You can use decoder to decode it.

.. code-block:: java

  BytesValue rawSignature = BytesValue
      .of("MEUCIQDP3ywVXX1DP42nTgM6cF95GFfpoEcl4D9ZP+MHO7SgoQIgdq2UiEiSp23lcPFzCHtDmh7pVzsow5x1s8p5Kz0aN7I=",
          Decoder.Base64);
  Signature signature = Signature.of(rawSignature);
  System.out.println(signature);
