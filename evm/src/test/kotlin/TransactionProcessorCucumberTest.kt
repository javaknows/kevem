package com.gammadex.kevin.evm

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    plugin = ["pretty"],
    features = ["src/test/resources/features/transaction_processor"],
    tags = ["not @Ignore"],
    //tags = ["@Only"],
    glue=["com.gammadex.kevin.evm.test.transaction_processor"]

)
class TransactionProcessorCucumberTest