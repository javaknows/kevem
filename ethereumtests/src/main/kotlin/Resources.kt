package org.kevm.ethereumtests

fun loadFromClasspath(path: String): String {
    return VMTestCaseParser::class.java.classLoader.getResource(path)
        ?.readText()
        ?: throw IllegalStateException("not found on classpath - $path")
}