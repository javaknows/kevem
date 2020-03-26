package org.kevem.evm.locking

import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantLock

// TODO - move to common

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

fun <T> locked(lock: ReentrantLock, op: () -> T): T {
    lock.lock()
    try {
        return op()
    } finally {
        lock.unlock()
    }
}