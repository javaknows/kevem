package org.kevm.evm

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    plugin = ["pretty"],
    features = ["src/test/resources/features/executor"],
    //tags = ["not @Ignore"],
    tags = ["@Only"],
    glue=["org.kevm.evm.test.executor"]

)
class ExecutorCucumberTest