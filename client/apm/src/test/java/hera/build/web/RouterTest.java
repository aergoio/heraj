package hera.build.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hera.AbstractTestCase;
import hera.build.web.service.ContractService;
import javax.inject.Inject;
import org.junit.Test;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest(classes = RouterTestConfig.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@ActiveProfiles({"test"})
public class RouterTest extends AbstractTestCase {
  @Inject
  protected MockMvc mvc;

  @Inject
  protected ContractService contractService;

  @Test
  public void testQuery() throws Exception {
    mvc.perform(get("/contract/3A332783A8E60202FA790C486767F99BAB430028578B145D8D552DFDD40B9C11/max?arguments=13&arguments=4544")).andExpect(status().isOk());
    verify(contractService).query(anyString(), anyString(), any());
  }

}