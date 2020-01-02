package org.kevm.evm

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    plugin = ["pretty"],
    features = ["src/test/resources/features/transaction_processor"],
    tags = ["not @Ignore"],
    //tags = ["@Only"],
    glue=["org.kevm.evm.test.transaction_processor"]
)
// TODO - possibly get rid of this in favour of only StatefulTransactionProcessorCucumberTest
class TransactionProcessorCucumberTest