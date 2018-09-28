/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.api.SignLocalTemplate;
import hera.api.model.Account;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.util.HexUtils;
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
    sender = accountTemplate.create(PASSWORD).getResult();
    recipient = accountTemplate.create(PASSWORD).getResult();
    transactionTemplate = new TransactionTemplate(channel);
  }

  @Test
  public void testSignAndCommit() {
    final Boolean unlockResult =
        accountTemplate.unlock(Authentication.of(sender.getAddress(), PASSWORD)).getResult();
    assertTrue(unlockResult);

    final Transaction transaction = new Transaction();
    transaction.setNonce(1);
    transaction.setAmount(30);
    transaction.setSender(sender.getAddress());
    transaction.setRecipient(recipient.getAddress());

    Signature signature = transactionTemplate.sign(transaction).getResult();
    assertNotNull(signature);
    assertNotNull(signature.getSign());
    assertNotNull(signature.getTxHash());
    logger.debug("Signature: {}", signature);

    transaction.setSignature(signature);
    final Hash hash = transactionTemplate.commit(transaction).getResult();
    logger.debug("Hash: {}", hash);
    assertNotNull(hash);

    final Transaction queried =
        transactionTemplate.getTransaction(signature.getTxHash()).getResult();
    assertNotNull(queried);
    assertEquals(sender.getAddress(), queried.getSender());
    assertEquals(recipient.getAddress(), queried.getRecipient());
    logger.debug("Transaction: {}", queried);

    final Boolean lockResult =
        accountTemplate.lock(Authentication.of(sender.getAddress(), PASSWORD)).getResult();
    assertTrue(lockResult);
  }

  @Test
  public void testSignLocallyAndCommit() throws Exception {
    final AergoKey key = new AergoKeyGenerator().create();

    final Transaction transaction = new Transaction();
    transaction.setNonce(1);
    transaction.setAmount(30);
    transaction.setSender(key.getAddress());
    transaction.setRecipient(recipient.getAddress());

    final SignLocalTemplate signTemplate = new SignLocalTemplate(key);
    final Signature signature = signTemplate.sign(transaction).getResult();
    transaction.setSignature(signature);
    logger.debug("Signature: {}", transaction.getSignature());

    final Transaction queried = transactionTemplate.commit(transaction)
        .flatMap(r -> transactionTemplate.getTransaction(signature.getTxHash())).getResult();
    assertNotNull(queried);
    assertEquals(key.getAddress(), queried.getSender());
    assertEquals(recipient.getAddress(), queried.getRecipient());
    logger.debug("Transaction: {}", queried);
  }

  @Test
  public void testSend() {
    final Boolean unlockResult =
        accountTemplate.unlock(Authentication.of(sender.getAddress(), PASSWORD)).getResult();
    assertTrue(unlockResult);

    final Transaction transaction = new Transaction();
    transaction.setAmount(30);
    transaction.setSender(sender.getAddress());
    transaction.setRecipient(recipient.getAddress());

    final TxHash hash = transactionTemplate.send(transaction).getResult();
    logger.debug("Hash: {}", HexUtils.encode(hash.getValue()));
    assertNotNull(hash);

    final Transaction queried = transactionTemplate.getTransaction(hash).getResult();
    assertNotNull(queried);
    assertEquals(sender.getAddress(), queried.getSender());
    assertEquals(recipient.getAddress(), queried.getRecipient());
    logger.debug("Transaction: {}", queried);

    final Boolean lockResult =
        accountTemplate.lock(Authentication.of(sender.getAddress(), PASSWORD)).getResult();
    assertTrue(lockResult);
  }

}
