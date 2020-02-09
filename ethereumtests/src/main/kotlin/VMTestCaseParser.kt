package org.kevm.ethereumtests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

data class Env(
    val currentCoinbase: String,
    val currentDifficulty: String,
    val currentGasLimit: String,
    val currentNumber: String,
    val currentTimestamp: String
)

data class Exec(
    val address: String,
    val caller: String,
    val code: String,
    val data: String,
    val gas: String,
    val gasPrice: String,
    val origin: String,
    val value: String
)

data class TestCaseAccount(
    val balance: String,
    val code: String,
    val nonce: String,
    val storage: Map<String, String>
)

data class Info(
    val comment: String,
    val filledwith: String,
    val lllcversion: String,
    val source: String,
    val sourceHash: String
)

data class VMTestCase(
    @JsonProperty("_info")
    val info: Info,
    val callcreates: List<String>?,
    val env: Env,
    val exec: Exec,
    val gas: String?,
    val logs: String?,
    val out: String?,
    val post: Map<String, TestCaseAccount>?,
    val pre: Map<String, TestCaseAccount>
) {
    val name = info.source.replace("src/VMTestsFiller/", "").replace("Filler.json", "")

    override fun toString(): String = name
}

class VMTestCaseParser {

    private val mapper = ObjectMapper().registerKotlinModule()

    fun parse(path: String): VMTestCase =
        parseJson(loadJson(path))

    private fun parseJson(json: String) =
        mapper.readValue(json, object : TypeReference<Map<String, VMTestCase>>() {}).values.first()

    private fun loadJson(requestPath: String): String = loadFromClasspath("ethereum-tests-pack/VMTests/$requestPath")

}

