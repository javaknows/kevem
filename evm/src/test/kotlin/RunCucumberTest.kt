package com.gammadex.kevin

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    plugin = ["pretty"],
    features = ["src/test/resources/cucumber-features"],
    tags = ["not @Ignore"]
)
class RunCucumberTest