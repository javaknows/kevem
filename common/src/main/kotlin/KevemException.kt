package org.kevem.common

open class KevemException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

open class CategorisedKevemException(message: String, val code: Long, cause: Throwable? = null) : KevemException(message, cause)
