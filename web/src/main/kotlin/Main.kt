package org.kevm.web

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    Server().start(9001)
    exitProcess(0)
}
