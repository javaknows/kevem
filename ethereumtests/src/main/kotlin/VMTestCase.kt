package org.kevm.ethereumtests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

data class VMTestCaseEnv(
    val currentCoinbase: String,
    val currentDifficulty: String,
    val currentGasLimit: String,
    val currentNumber: String,
    val currentTimestamp: String
)

data class VMTestCaseExec(
    val address: String,
    val caller: String,
    val code: String,
    val data: String,
    val gas: String,
    val gasPrice: String,
    val origin: String,
    val value: String
)

data class VMTestCaseAccount(
    val balance: String,
    val code: String,
    val nonce: String,
    val storage: Map<String, String>
)

data class VMTestCaseInfo(
    val comment: String,
    val filledwith: String,
    val lllcversion: String,
    val source: String,
    val sourceHash: String
)

data class VMTestCase(
    @JsonProperty("_info")
    val info: VMTestCaseInfo,
    val callcreates: List<String>?,
    val env: VMTestCaseEnv,
    val exec: VMTestCaseExec,
    val gas: String?,
    val logs: String?,
    val out: String?,
    val post: Map<String, VMTestCaseAccount>?,
    val pre: Map<String, VMTestCaseAccount>
) {
    val name = info.source.replace("src/VMTestsFiller/", "").replace("Filler.json", "")

    override fun toString(): String = name
}
