package hera;

import hera.client.AccountTemplateIT;
import hera.client.BlockTemplateIT;
import hera.client.TransactionTemplateIT;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    AccountTemplateIT.class,
    BlockTemplateIT.class,
    TransactionTemplateIT.class
})
public class ITTests {

}
