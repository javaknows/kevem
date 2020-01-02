import org.kevm.evm.StatefulTransactionProcessor
import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    plugin = ["pretty"],
    features = ["src/test/resources/features/stateful_transaction_processor"],
    tags = ["not @Ignore"],
    //tags = ["@Only"],
    glue = ["org.kevm.evm.test.transaction_processor"]
)
class StatefulTransactionProcessorCucumberTest {

}