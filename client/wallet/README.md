# What is wallet?
It's the key pair storage for multiple purpose. It provides next:
* key creation
* Sign the text with public key
* Create transaction

# Kind of wallet
## Naive wallet
The NaiveWallet is simple implementation. The key is stored the KeyStorage, which is abstraction for key store.

## SecuredWallet
The SecuredWallet is NaiveWallet's specific implementation using java.security.KeyStore. It guarantees more safe key management.
