package org.kevem.rpc

import org.kevem.evm.locking.readLock
import org.kevem.evm.locking.writeLock
import org.kevem.evm.model.Address
import org.kevem.common.Byte
import org.kevem.common.conversions.toByteList
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

data class LocalAccount(val address: Address, val privateKey: List<Byte>, val locked: Boolean = false) {
    constructor(address: String, privateKey: String, locked: Boolean = false) : this(
        Address(address),
        toByteList(privateKey),
        locked
    )
}

class LocalAccounts(a: List<LocalAccount> = emptyList()) {

    private val lock: ReadWriteLock = ReentrantReadWriteLock()

    private var acc = a

    var accounts
        get() = readLock(lock) {
            acc
        }
        set(value) = writeLock(lock) {
            acc = value
        }

    fun getByAddress(address: Address): LocalAccount? = accounts.find { it.address == address }

}