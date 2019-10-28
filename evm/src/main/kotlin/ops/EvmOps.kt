package com.gammadex.kevin.evm.ops

import com.gammadex.kevin.evm.model.ExecutionContext
import com.gammadex.kevin.evm.model.Log


fun push(context: ExecutionContext, numBytes: Int): ExecutionContext {
    val call = context.callStack.last()
    val data = call.contract.code.subList(context.currentLocation + 1, context.currentLocation + 1 + numBytes)
    val newStack = context.stack.push(data)

    return context.updateCurrentCallCtx(stack = newStack)
}

fun dup(context: ExecutionContext, offset: Int): ExecutionContext {
    val data = context.stack.peek(offset)
    val newStack = context.stack.push(data)

    return context.updateCurrentCallCtx(stack = newStack)
}

fun swap(context: ExecutionContext, offset: Int): ExecutionContext {
    val a = context.stack.peekWord(offset + 1)
    val b = context.stack.peekWord()
    val newStack = context.stack
        .set(offset + 1, b.data)
        .set(0, a.data)

    return context.updateCurrentCallCtx(stack = newStack)
}

fun log(context: ExecutionContext, num: Int): ExecutionContext = with(context) {
    val (topics, newStack) = stack.popWords(num)
    val (elements, newStack2) = newStack.popWords(2)
    val (p, s) = elements.map { it.toInt() }
    val data = memory.get(p, s)
    val newLog = Log(data, topics)

    return context
        .updateCurrentCallCtx(stack = newStack2)
        .copy(logs = context.logs + newLog)
}

fun pop(context: ExecutionContext): ExecutionContext = with(context) {
    val (_, newStack) = stack.pop()

    context.updateCurrentCallCtx(stack = newStack)
}
