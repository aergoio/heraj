# What is wallet?

It's the key pair storage for multiple purpose. It provides next:
* Key save and load
* Sign the raw transaction
* Create transaction
* Send aergo
* Deploy and execute smart contract
* Nonce handling

# Kind of wallet

## Naive
The Naive type is simple implementation. The key is stored in the in-memory keystore.

## Secure
The Secure type is NaiveWallet's specific implementation using java.security.KeyStore. It guarantees more safe key management.

## ServerKeyStore
The ServerKeyStore type is an wallet using server keystore.
