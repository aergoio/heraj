package hera.keystore;

import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.key.AergoKey;
import hera.key.Signer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static hera.util.ValidationUtils.*;

/**
 * CachedKeystore is a utility keystore that can load the key stored in the original keystore,
 * and can save key in temporarily in memory.
 * It doesn't modify the original keystore specified as baseKeyStore.
 *
 */
public class CachedKeyStore extends AbstractKeyStore implements KeyStore {
  protected final AbstractKeyStore baseKeyStore;
  protected final InMemoryKeyStore memoryKeyStore = new InMemoryKeyStore();

  public CachedKeyStore(KeyStore baseKeyStore) {
    assertNotNull(baseKeyStore);
    assertFalse(this == baseKeyStore);
    assertTrue(baseKeyStore instanceof AbstractKeyStore);
    this.baseKeyStore = (AbstractKeyStore) baseKeyStore;
  }

  @Override
  public void save(Authentication authentication, AergoKey key) {
    memoryKeyStore.save(authentication, key);
  }

  @Override
  public Signer load(Authentication authentication) {
    return loadAergoKey(authentication);
  }

  @Override
  protected AergoKey loadAergoKey(Authentication authentication) {
    if( memoryKeyStore.storedIdentities.contains(authentication.getIdentity()) ){
      return memoryKeyStore.loadAergoKey(authentication);
    } else {
      AergoKey loadedKey = baseKeyStore.loadAergoKey(authentication);
      memoryKeyStore.save(authentication, loadedKey);
      return loadedKey;
    }
  }

  @Override
  public void remove(Authentication authentication) {
    // it only removes key from memory. so it can be resurrected by baseKeyStore
    memoryKeyStore.remove(authentication);
  }

  @Override
  public EncryptedPrivateKey export(Authentication authentication, String password) {
    return memoryKeyStore.export(authentication, password);
  }

  @Override
  public List<Identity> listIdentities() {
    HashSet<Identity> set = new HashSet<>(memoryKeyStore.listIdentities());
    set.addAll(baseKeyStore.listIdentities());
    return new ArrayList<>(set);
  }

  @Override
  public boolean contains(Identity identity) {
    return memoryKeyStore.contains(identity) || baseKeyStore.contains(identity);
  }

  @Override
  public void store(String path, char[] password) {
    // do nothing
  }
}
