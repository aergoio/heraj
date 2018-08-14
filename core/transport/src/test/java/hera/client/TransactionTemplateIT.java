/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.api.SignTemplate;
import hera.api.model.Account;
import hera.api.model.BytesValue;
import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSAKeyGenerator;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class TransactionTemplateIT extends AbstractIT {

  protected static final String PASSWORD = randomUUID().toString();

  protected Account sender = null;
  protected Account recipient = null;

  protected AccountTemplate accountTemplate = null;

  protected TransactionTemplate transactionTemplate = null;

  @Before
  public void setUp() {
    super.setUp();
    accountTemplate = new AccountTemplate(channel);
    sender = accountTemplate.create(PASSWORD);
    recipient = accountTemplate.create(PASSWORD);
    accountTemplate.unlock(sender.getAddress(), PASSWORD);
    transactionTemplate = new TransactionTemplate(channel);
  }

  @Test
  public void testSignAndCommit() {
    final Transaction transaction = new Transaction();
    transaction.setNonce(1);
    transaction.setAmount(30);
    transaction.setSender(sender.getAddress());
    transaction.setRecipient(recipient.getAddress());
    Signature signature = transactionTemplate.sign(transaction);
    logger.debug("Signature: {}", signature);

    assertNotNull(signature);
    assertNotNull(signature.getSign());
    assertNotNull(signature.getHash());
    transaction.setSignature(signature);
    transactionTemplate.commit(transaction);

    final Optional<Transaction> queried = transactionTemplate.getTransaction(signature.getHash());
    assertTrue(queried.isPresent());
    assertEquals(sender.getAddress(), queried.get().getSender());
    assertEquals(recipient.getAddress(), queried.get().getRecipient());
    logger.debug("Transaction: {}", queried.get());
  }

  @Test
  public void testSignLocallyAndCommit() throws Exception {
    final Transaction transaction = new Transaction();
    transaction.setNonce(1);
    transaction.setAmount(30);
    transaction.setSender(sender.getAddress());
    transaction.setRecipient(recipient.getAddress());

    final SignTemplate signTemplate = new SignTemplate();

    final Hash hashWithoutSign = transaction.calculateHash();
    final ECDSAKey key = new ECDSAKeyGenerator().create();
    final BytesValue sign = signTemplate.sign(key, hashWithoutSign);
    transaction.setSignature(Signature.of(sign, null));
    final Hash hash = transaction.calculateHash();

    final Signature signature = Signature.of(sign, hash);
    logger.debug("Signature: {}", signature);
    transaction.setSignature(signature);

    transactionTemplate.commit(transaction);

    final Optional<Transaction> queried = transactionTemplate.getTransaction(signature.getHash());
    assertTrue(queried.isPresent());
    assertEquals(sender.getAddress(), queried.get().getSender());
    assertEquals(recipient.getAddress(), queried.get().getRecipient());
    logger.debug("Transaction: {}", queried.get());
  }
}