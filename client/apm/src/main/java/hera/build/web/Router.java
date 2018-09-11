/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web;

import hera.build.web.exception.ResourceNotFoundException;
import hera.build.web.model.BuildDetails;
import hera.build.web.model.BuildSummary;
import hera.build.web.model.ContractInput;
import hera.build.web.model.DeploymentResult;
import hera.build.web.model.ExecutionResult;
import hera.build.web.model.QueryResult;
import hera.build.web.service.BuildService;
import hera.build.web.service.ContractService;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Router {
  @Inject
  protected BuildService buildService;

  @Inject
  protected ContractService contractService;

  @GetMapping("contract")
  public DeploymentResult getLatestContract() {
    return contractService.getLatestContractInformation();
  }

  @GetMapping("builds")
  public List<BuildSummary> getLatestBuilds() {
    return buildService.list(null, 5);
  }

  @GetMapping("build/{uuid}")
  public BuildDetails getBuild(@PathVariable("uuid") final String buildUuid) {
    return buildService.get(buildUuid).orElseThrow(ResourceNotFoundException::new);
  }

  /**
   * Deploy result of build with {@code buildUuid}.
   *
   * @param buildUuid build uuid
   *
   * @return deployment result
   *
   * @throws Exception Fail to deploy
   */
  @PostMapping("build/{uuid}/deploy")
  public DeploymentResult deploy(@PathVariable("uuid") final String buildUuid) throws Exception {
    final BuildDetails buildDetails = buildService.get(buildUuid)
        .orElseThrow(() -> new ResourceNotFoundException(buildUuid + " not found"));
    return contractService.deploy(buildDetails);
  }

  @PostMapping(value = "contract/{tx}/{function}")
  public ExecutionResult execute(
      @PathVariable("tx") final String contractTransactionHash,
      @PathVariable("function") final String functionName,
      @RequestBody final ContractInput contractInput) throws IOException {
    final String[] arguments = contractInput.getArguments();
    return contractService.execute(contractTransactionHash, functionName, arguments);
  }

  @GetMapping(value = "contract/{tx}/{function}")
  public QueryResult query(
      @PathVariable("tx") final String contractTransactionHash,
      @PathVariable("function") final String functionName,
      @RequestParam("arguments") final String[] arguments) throws IOException {
    return contractService.query(contractTransactionHash, functionName, arguments);
  }
}
