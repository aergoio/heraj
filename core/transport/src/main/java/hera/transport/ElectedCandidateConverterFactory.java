/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.parseToAer;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.model.ElectedCandidate;
import hera.util.Base58Utils;
import org.slf4j.Logger;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
public class ElectedCandidateConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<ElectedCandidate, Rpc.Vote> domainConverter =
      new Function1<ElectedCandidate, Rpc.Vote>() {

        @Override
        public Rpc.Vote apply(final ElectedCandidate domainVote) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Rpc.Vote, ElectedCandidate> rpcConverter =
      new Function1<Rpc.Vote, ElectedCandidate>() {

        @Override
        public ElectedCandidate apply(final Rpc.Vote rpcElectedCandidate) {
          logger.trace("Rpc vote status to convert: {}", rpcElectedCandidate);
          final ElectedCandidate domainElectedCandidate = ElectedCandidate.newBuilder()
              .candidateId(Base58Utils.encode(rpcElectedCandidate.getCandidate().toByteArray()))
              .voted(parseToAer(rpcElectedCandidate.getAmount()))
              .build();
          logger.trace("Domain vote status converted: {}", domainElectedCandidate);
          return domainElectedCandidate;
        }
      };

  public ModelConverter<ElectedCandidate, Rpc.Vote> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
