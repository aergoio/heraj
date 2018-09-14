/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TransactionType;
import hera.util.Base58Utils;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

public class TransactionTest {

  protected final transient Logger logger = getLogger(getClass());

  // base58 encoded "sender"
  protected static final String SENDER = "zTuiFm13";

  // base58 encoded "recipient"
  protected static final String RECIPIENT = "2TTMxAeyKKB71";

  // base58 encoded "payload"
  protected static final String PAYLOAD = "5G1Mm97rxT";

  // base58 encoded "signature"
  protected static final String SIGNATURE =
      "7yDUQzV8VJSuUkXMdkKpPmQTGnSfvaZArB6YifZfJRxErsTcsFaFr3ENVcV8sM7p24NZq6KsxzwrSeUNqLttwL2";

  // base58 encoded "hash"
  protected static final String HASH_WITHOUT_SIGN = "Area2ukmPb4e7zvip8hCnM3eLuUygmfnfUJ9eqHg5reQ";

  // base58 encoded "hash with sign"
  protected static final String HASH_WITH_SIGN = "8R2hTc6MqSMmPCM3anWFYbCu8JnzKWrHoP3HV6MUUetX";


  protected Transaction transaction = null;

  @Before
  public void setup() {
    transaction = new Transaction();
    transaction.setNonce(1L);
    transaction.setSender(AccountAddress.of(Base58Utils.decode(SENDER)));
    transaction.setRecipient(AccountAddress.of(Base58Utils.decode(RECIPIENT)));
    transaction.setAmount(1L);
    transaction.setPayload(BytesValue.of(Base58Utils.decode(PAYLOAD)));
    transaction.setLimit(1L);
    transaction.setPrice(1L);
    transaction.setTxType(TransactionType.NORMAL);
  }

  @Test
  public void testEquals() {
    final byte[] byteValue1 = randomUUID().toString().getBytes();
    final byte[] byteValue2 = Arrays.copyOf(byteValue1, byteValue1.length);

    final BytesValue value1 = new BytesValue(byteValue1);
    final BytesValue value2 = new BytesValue(byteValue2);

    assertEquals(value1, value2);
  }

  @Test
  public void testCalculateHashWithoutSign() throws Exception {
    final String expected = HASH_WITHOUT_SIGN;
    final String actual = transaction.calculateHash().getEncodedValue();
    assertEquals(expected, actual);
  }

  @Test
  public void testCalculateHashWithign() throws Exception {
    transaction.setSignature(Signature.of(BytesValue.of(Base58Utils.decode(SIGNATURE)), null));
    final String expected = HASH_WITH_SIGN;
    final String actual = transaction.calculateHash().getEncodedValue();
    assertEquals(expected, actual);
  }

}
