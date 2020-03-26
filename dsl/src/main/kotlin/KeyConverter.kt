package org.kevem.dsl

import org.web3j.crypto.Credentials

fun privateKeyToAddress(privatekey: String): String = Credentials.create(privatekey).address
