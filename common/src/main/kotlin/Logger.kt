package org.kevem.common

import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

private val lineCounter = AtomicInteger()

class Logger(private val clazz: KClass<*>) {

    companion object Logger {
        fun createLogger(clazz: KClass<*>) = Logger(clazz)
    }

    fun info(msg: String) = println("${lineCounter.incrementAndGet()}\tINFO ${clazz.qualifiedName} $msg")

    fun debug(msg: String) = println("${lineCounter.incrementAndGet()}\tDEBUG ${clazz.qualifiedName} $msg")

    fun trace(msg: String) = println("${lineCounter.incrementAndGet()}\tTRACE ${clazz.qualifiedName} $msg")

    fun error(msg: String) = println("${lineCounter.incrementAndGet()}\tERROR ${clazz.qualifiedName} $msg")

}
