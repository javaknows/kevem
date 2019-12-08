package com.gammadex.kevin.rpc

import com.gammadex.kevin.evm.locking.readLock
import com.gammadex.kevin.evm.locking.writeLock
import com.gammadex.kevin.evm.model.Address
import com.gammadex.kevin.evm.model.Byte
import org.web3j.abi.datatypes.Bool
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

data class LocalAccount(val address: Address, val privateKey: List<Byte>, val locked: Boolean = false)

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

    fun getByAddress(address: Address): LocalAccount? = accounts.find { it.address ==  address}

}