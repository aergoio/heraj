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
  public void testCreate() {
    final List<RpcCommitException> commitExceptions = new ArrayList<RpcCommitException>();
    for (final Rpc.CommitStatus rpcCommitStatus : Rpc.CommitStatus.values()) {
      commitExceptions.add(new RpcCommitException(new InternalCommitException(rpcCommitStatus, "")));
    }
    Iterator<RpcCommitException> it = commitExceptions.iterator();
    for (final RpcCommitException.CommitStatus expected : RpcCommitException.CommitStatus
        .values()) {
      final RpcCommitException next = it.next();
      assertEquals(expected, next.commitStatus);
    }
    assertTrue(!it.hasNext());
  }

}
