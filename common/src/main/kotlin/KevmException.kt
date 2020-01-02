package org.kevm.common

open class KevmException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)