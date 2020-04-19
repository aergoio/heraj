/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.Context;
import hera.ContextStorage;
import hera.EmptyContext;
import hera.Invocation;
import hera.Requester;
import hera.WriteSynchronizedContextStorage;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

public class KeyStoreTemplateTest extends AbstractTestCase {

  protected final ContextStorage<Context> contextStorage = new WriteSynchronizedContextStorage<>();

  protected RawTransaction anyRawTransaction;

  protected Transaction anyTransaction;

  protected Identity anyIdentity;

  {
    contextStorage.put(EmptyContext.getInstance());
    final AergoKey signer = new AergoKeyGenerator().create();
    anyRawTransaction = RawTransaction
        .newBuilder(ChainIdHash.of(BytesValue.EMPTY))
        .from(signer.getAddress())
        .to(signer.getAddress())
        .amount(Aer.ZERO)
        .nonce(1L)
        .build();
    anyTransaction = signer.sign(anyRawTransaction);
    anyIdentity = new Identity() {
      @Override
      public String getValue() {
        return randomUUID().toString();
      }
    };
  }

  @Test
  public void testList() throws Exception {
    // given
    final KeyStoreTemplate keystoreTemplate = new KeyStoreTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final List<AccountAddress> expected = emptyList();
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    keystoreTemplate.requester = mockRequester;

    // then
    final List<AccountAddress> actual = keystoreTemplate.list();
    assertEquals(expected, actual);
  }

  @Test
  public void testCreate() throws Exception {
    // given
    final KeyStoreTemplate keystoreTemplate = new KeyStoreTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final AccountAddress expected = AccountAddress.EMPTY;
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    keystoreTemplate.requester = mockRequester;

    // then
    final AccountAddress actual = keystoreTemplate.create(randomUUID().toString());
    assertEquals(expected, actual);
  }

  @Test
  public void testLock() throws Exception {
    // given
    final KeyStoreTemplate keystoreTemplate = new KeyStoreTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final boolean expected = false;
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    keystoreTemplate.requester = mockRequester;

    // then
    final boolean actual = keystoreTemplate
        .lock(Authentication.of(anyIdentity, randomUUID().toString()));
    assertEquals(expected, actual);
  }

  @Test
  public void testUnlock() throws Exception {
    // given
    final KeyStoreTemplate keystoreTemplate = new KeyStoreTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final boolean expected = false;
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    keystoreTemplate.requester = mockRequester;

    // then
    final boolean actual = keystoreTemplate
        .unlock(Authentication.of(anyIdentity, randomUUID().toString()));
    assertEquals(expected, actual);
  }

  @Test
  public void testSign() throws Exception {
    // given
    final KeyStoreTemplate keystoreTemplate = new KeyStoreTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final Transaction expected = anyTransaction;
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    keystoreTemplate.requester = mockRequester;

    // then
    final Transaction actual = keystoreTemplate.sign(anyRawTransaction);
    assertEquals(expected, actual);
  }

  @Test
  public void testImportKey() throws Exception {
    // given
    final KeyStoreTemplate keystoreTemplate = new KeyStoreTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final AccountAddress expected = AccountAddress.EMPTY;
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    keystoreTemplate.requester = mockRequester;

    // then
    final AccountAddress actual = keystoreTemplate
        .importKey(EncryptedPrivateKey.EMPTY, randomUUID().toString(), randomUUID().toString());
    assertEquals(expected, actual);
  }

  @Test
  public void testExportKey() throws Exception {
    // given
    final KeyStoreTemplate keystoreTemplate = new KeyStoreTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final EncryptedPrivateKey expected = EncryptedPrivateKey.EMPTY;
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    keystoreTemplate.requester = mockRequester;

    // then
    final EncryptedPrivateKey actual = keystoreTemplate
        .exportKey(Authentication.of(anyIdentity, randomUUID().toString()));
    assertEquals(expected, actual);
  }

  @Test
  public void testSend() throws Exception {
    // given
    final KeyStoreTemplate keystoreTemplate = new KeyStoreTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final TxHash expected = TxHash.of(BytesValue.EMPTY);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    keystoreTemplate.requester = mockRequester;

    // then
    final TxHash actual = keystoreTemplate
        .send(AccountAddress.EMPTY, AccountAddress.EMPTY, Aer.AERGO_ONE, BytesValue.EMPTY);
    assertEquals(expected, actual);
  }

}
