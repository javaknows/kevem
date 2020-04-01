package org.kevem.ethereumtests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.kevem.common.loadFromClasspath

data class GeneralStateTestsFillerEnv(
    val currentCoinbase: String,
    val currentDifficulty: String,
    val currentGasLimit: String,
    val currentNumber: String,
    val currentTimestamp: String,
    val previousHash: String
)

data class GeneralStateTestsFillerIndexArray(
     val data: List<Int>,
     val gas: List<Int>,
     val value: List<Int>
)

data class GeneralStateTestsFillerExpect(
    val indexes: GeneralStateTestsFillerIndexArray,
    val network: List<String>,
    val result: Map<String, GeneralStateTestsFillerResult>,
    @JsonProperty("//comment")
    val comment: String?
)

data class GeneralStateTestsFillerPre(
    val balance: String,
    val nonce: String,
    val code: String,
    val storage: Map<String, String>,
    @JsonProperty("//code")
    val sourceCode: String?,
    @JsonProperty("// code")
    val sourceCode2: String?,
    @JsonProperty("//0 code")
    val codeComments0: String?,
    @JsonProperty("//1 code")
    val codeComments1: String?,
    @JsonProperty("//2 code")
    val codeComments2: String?,
    @JsonProperty("//3 code")
    val codeComments3: String?,
    @JsonProperty("//")
    val comment: String?
)

data class GeneralStateTestsFillerTransaction(
    val data: List<String>,
    val gasLimit: List<String>,
    val gasPrice: String,
    val nonce: String,
    val secretKey: String,
    val to: String,
    val value: List<String>,
    @JsonProperty("// data")
    val dataComment: List<String>?
)

data class GeneralStateTestsFillerResult(
    val balance: String?,
    val nonce: String?,
    val code: String?,
    val storage: Map<String, String>?,
    val shouldnotexist: String?
)

data class GeneralStateTestsFillerInfo(
    val comment: String?
)

data class GeneralStateTestsFiller(
    @JsonProperty("_info")
    val info: GeneralStateTestsFillerInfo?,
    val env: GeneralStateTestsFillerEnv,
    val expect: List<GeneralStateTestsFillerExpect>,
    val pre: Map<String, GeneralStateTestsFillerPre>,
    val transaction: GeneralStateTestsFillerTransaction
)

class GeneralStateTestsFillerLoader(private val root: String) {

    private val mapper = ObjectMapper(YAMLFactory())
        .registerKotlinModule()
        .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private val typeReference = object : TypeReference<Map<String, GeneralStateTestsFiller>>() {}

    fun parse(path: String): GeneralStateTestsFiller {
        return try {
            return parseYml(loadYml(path))
        } catch (e: Exception) {
            println(path)
            throw e
        }
    }

    private fun parseYml(json: String) = mapper.readValue(json.replace("\t", ""), typeReference).values.first()

    private fun loadYml(requestPath: String): String =
        loadFromClasspath("$root/$requestPath")
}

