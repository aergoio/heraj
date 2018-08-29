/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.Hash;
import hera.api.model.Receipt;
import hera.api.model.Transaction;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Test;

public class ContractTemplateIT extends AbstractIT {

  protected static final String CONTRACT_CODE = "src/test/resources/contract.luabc";

  protected static final String EXECUTION_CODE = "src/test/resources/execution.json";

  protected static final String PASSWORD = randomUUID().toString();

  protected final AtomicInteger atomicInteger = new AtomicInteger(1);

  protected Account creator = null;

  protected AccountTemplate accountTemplate = null;

  protected TransactionTemplate transactionTemplate = null;

  protected ContractTemplate contractTemplate = null;

  @Before
  public void setUp() {
    super.setUp();
    accountTemplate = new AccountTemplate(channel);
    creator = accountTemplate.create(PASSWORD).getResult();
    transactionTemplate = new TransactionTemplate(channel);
    contractTemplate = new ContractTemplate(channel);
  }

  protected byte[] readBytesFromFile(final String filename) throws IOException {
    final File file = new File(filename);
    assertTrue(file.exists());
    return Files.readAllBytes(file.toPath());
  }

  @Test
  public void testLuaContractDeployAndExecuteByPayload() throws Throwable {
    final Boolean unlockResult = accountTemplate.unlock(creator.getAddress(), PASSWORD).getResult();
    assertTrue(unlockResult);

    final Transaction definitionTransaction = new Transaction();
    definitionTransaction.setNonce(atomicInteger.getAndIncrement());
    definitionTransaction.setSender(creator.getAddress());
    definitionTransaction.setPayload(BytesValue.of(readBytesFromFile(CONTRACT_CODE)));

    definitionTransaction.setSignature(transactionTemplate.sign(definitionTransaction).getResult());
    final Hash definitionTxHash = transactionTemplate.commit(definitionTransaction).getResult();
    assertNotNull(definitionTxHash);
    logger.debug("Hash: {}", definitionTxHash);

    waitForNextBlockToGenerate();

    final Receipt definitionReceipt = contractTemplate.getReceipt(definitionTxHash).getResult();
    assertTrue(0 < definitionReceipt.getReceipt().getValue().length);
    assertEquals("CREATED", definitionReceipt.getStatus());

    final AccountAddress contractAddress = definitionReceipt.getReceipt();
    logger.debug("ContractAddress: {}", contractAddress);

    final Transaction executionTransaction = new Transaction();
    executionTransaction.setNonce(atomicInteger.getAndIncrement());
    executionTransaction.setSender(creator.getAddress());
    executionTransaction.setRecipient(contractAddress);
    executionTransaction.setPayload(BytesValue.of(readBytesFromFile(EXECUTION_CODE)));

    executionTransaction.setSignature(transactionTemplate.sign(executionTransaction).getResult());
    final Hash executionTxHash = transactionTemplate.commit(executionTransaction).getResult();
    assertNotNull(executionTxHash);
    logger.debug("Hash: {}", executionTxHash);

    waitForNextBlockToGenerate();

    final Receipt executionReceipt = contractTemplate.getReceipt(executionTxHash).getResult();
    assertTrue(0 < executionReceipt.getReceipt().getValue().length);
    assertEquals("SUCCESS", executionReceipt.getStatus());

    final Boolean lockResult = accountTemplate.lock(creator.getAddress(), PASSWORD).getResult();
    assertTrue(lockResult);
  }
}
