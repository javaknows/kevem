package com.gammadex.kevin.evm.locking

import java.util.concurrent.locks.ReadWriteLock

fun <T> writeLock(lock: ReadWriteLock, op: () -> T): T {
    lock.writeLock().lock()
    try {
        return op()
    } finally {
        lock.writeLock().unlock()
    }
}

fun <T> readLock(lock: ReadWriteLock, op: () -> T): T {
    lock.readLock().lock()
    try {
        return op()
    } finally {
        lock.readLock().unlock()
    }
}