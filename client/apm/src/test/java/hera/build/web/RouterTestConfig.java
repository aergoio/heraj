package hera.build.web;

import hera.build.web.service.BuildService;
import hera.build.web.service.ContractService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.web.WebAppConfiguration;

@ComponentScan(
    basePackageClasses = {Router.class},
    useDefaultFilters = false,
    includeFilters = @Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = Router.class)
)
@Configuration
@WebAppConfiguration
public class RouterTestConfig {
  @MockBean
  protected BuildService buildService;

  @MockBean
  protected ContractService contractService;

}
