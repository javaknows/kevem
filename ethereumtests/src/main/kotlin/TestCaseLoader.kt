package org.kevem.ethereumtests

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.lang.Exception

class TestCaseParser<T>(private val typeReference :TypeReference<Map<String, T>>, private val rootPath: String) {

    private val mapper = ObjectMapper().registerKotlinModule()

    fun parse(path: String): T =
        parseJson(loadJson(path))

    private fun parseJson(json: String) =
        mapper.readValue(json, typeReference).values.first()

    private fun loadJson(requestPath: String): String =
        loadFromClasspath("$rootPath/$requestPath")
}

class  TestCaseLoader<T>(private val parser: TestCaseParser<T>, private val rootPath: String) {

    fun loadTestCases(): List<T> {
        val blackList = loadFromClasspath("$rootPath-blacklist.txt")
            .split("\n")
            .map { it.trim() }
            .map{ it.replace("#.*".toRegex(), "") }
            .map { it.trim() }
            .filterNot { it == "" }

        val testNames = System.getProperty("testCase")?.let {
            listOf("$it.json")
        } ?: loadFromClasspath("$rootPath-list.txt")
            .split("\n")
            .map { it.trim() }
            .filterNot { tc -> blackList.any { b -> tc.contains(b) } }
            .filterNot { it.startsWith("#") }

        return testNames.flatMap {
            try {
                listOf(parser.parse(it))
            } catch (e: Exception) {
                println("failed to parse $it - ${e.message}")
                emptyList<T>()
            }
        }
    }
}