/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import types.Rpc;

public class TransactionVerificationExceptionTest extends AbstractTestCase {

  @Test
  public void testCreate() {
    List<TransactionVerificationException> verificationExceptions =
        new ArrayList<>();
    for (final Rpc.VerifyStatus rpcCommitStatus : Rpc.VerifyStatus.values()) {
      verificationExceptions.add(new TransactionVerificationException(rpcCommitStatus));
    }
    Iterator<TransactionVerificationException> it = verificationExceptions.iterator();
    for (final TransactionVerificationException.VerifyStatus expected : TransactionVerificationException.VerifyStatus
        .values()) {
      final TransactionVerificationException next = it.next();
      assertEquals(expected, next.verifyStatus);
      assertEquals(expected.toString(), next.getLocalizedMessage());
    }
    assertTrue(!it.hasNext());
  }

}
