package org.kevem.ethereumtests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

data class GeneralStateTestCaseEnv(
    val currentCoinbase: String,
    val currentDifficulty: String,
    val currentGasLimit: String,
    val currentNumber: String,
    val currentTimestamp: String,
    val previousHash: String
)

data class GeneralStateTestCaseTransaction(
    val data: List<String>,
    val gasLimit: List<String>,
    val gasPrice: String,
    val nonce: String,
    val secretKey: String,
    val to: String,
    val value: List<String>
)

data class GeneralStateTestCasePre(
    val balance: String,
    val nonce: String,
    val code: String,
    val storage: Map<String, String>
)

data class GeneralStateTestCasePost(
    val hash: String,
    val logs: String,
    val indexes: GeneralStateTestCaseIndex
)

data class GeneralStateTestCaseIndex(
    val data: String,
    val gas: String,
    val value: String
)

data class GeneralStateTestCaseInfo(
    val comment: String,
    val source: String,
    val sourceHash: String,
    @JsonProperty("filling-rpc-server")
    val fillingRpcServer: String,
    @JsonProperty("filling-tool-version")
    val fillingToolVersion: String,
    val lllcversion: String
)

data class GeneralStateTestCase(
    @JsonProperty("_info")
    val info: GeneralStateTestCaseInfo,
    val env: GeneralStateTestCaseEnv,
    val post: Map<String, List<GeneralStateTestCasePost>>,
    val pre: Map<String, GeneralStateTestCasePre>,
    val transaction: GeneralStateTestCaseTransaction
) {
    val name = info.source
        .replace("src/GeneralStateTestsFiller/", "")
        .replace("Filler.yml", "")
        .replace("Filler.json", "")

    override fun toString(): String = name
}

data class GeneralStateTestExplodedCaseTransaction(
    val data: String,
    val gasLimit: String,
    val gasPrice: String,
    val nonce: String,
    val secretKey: String,
    val to: String,
    val value: String
)

data class GeneralStateTestExplodedCase(
    val name: String,
    val env: GeneralStateTestCaseEnv,
    val post: List<GeneralStateTestCasePost>,
    val pre: Map<String, GeneralStateTestCasePre>,
    val transaction: GeneralStateTestExplodedCaseTransaction,
    val results: Map<String, GeneralStateTestsFillerResult>,
    val hardFork: String
) {
    override fun toString(): String = name
}
