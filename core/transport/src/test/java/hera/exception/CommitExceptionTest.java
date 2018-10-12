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

public class CommitExceptionTest extends AbstractTestCase {

  @Test
  public void testCreate() throws InterruptedException {
    List<CommitException> commitExceptions = new ArrayList<CommitException>();
    for (final Rpc.CommitStatus rpcCommitStatus : Rpc.CommitStatus.values()) {
      commitExceptions.add(new CommitException(rpcCommitStatus));
    }
    Iterator<CommitException> it = commitExceptions.iterator();
    for (final CommitException.CommitStatus expected : CommitException.CommitStatus
        .values()) {
      final CommitException next = it.next();
      assertEquals(expected, next.commitStatus);
      assertEquals(expected.toString(), next.getLocalizedMessage());
    }
    assertTrue(!it.hasNext());
  }

}
