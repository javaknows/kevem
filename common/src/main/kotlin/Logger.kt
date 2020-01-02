package org.kevm.common

import kotlin.reflect.KClass

class Logger(private val clazz: KClass<*>) {
    companion object Logger {
        fun createLogger(clazz: KClass<*>) = Logger(clazz)
    }

    fun info(msg: String) = println("INFO ${clazz.qualifiedName} $msg")

    fun trace(msg: String) = println("TRACE ${clazz.qualifiedName} $msg")

    fun error(msg: String) = println("ERROR ${clazz.qualifiedName} $msg")

}
