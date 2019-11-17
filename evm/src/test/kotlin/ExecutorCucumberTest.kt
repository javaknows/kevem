package com.gammadex.kevin.evm

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    plugin = ["pretty"],
    features = ["src/test/resources/debug-features"],
    tags = ["not @Ignore"]
)
class ExecutorCucumberTest