package com.gammadex.kevin


fun push(context: ExecutionContext, numBytes: Int): ExecutionContext {
    val call = context.callStack.last()
    val data = call.contract.code.subList(context.currentLocation + 1, context.currentLocation + 1 + numBytes)
    val newStack = context.stack.push(data)

    return context.updateCurrentCallContext(stack = newStack)
}

fun dup(context: ExecutionContext, offset: Int): ExecutionContext {
    val word = context.stack.peekWord(offset)
    val newStack = context.stack.pushWord(word)

    return context.updateCurrentCallContext(stack = newStack)
}

fun swap(context: ExecutionContext, offset: Int): ExecutionContext {
    val a = context.stack.peekWord(offset)
    val b = context.stack.peekWord(offset + 1)
    val newStack = context.stack
        .set(offset, b.data)
        .set(offset + 1, a.data)

    return context.updateCurrentCallContext(stack = newStack)
}

fun log(context: ExecutionContext, num: Int): ExecutionContext = with(context) {
    val (elements, newStack) = stack.popWords(2)
    val (p, s) = elements.map { it.toInt() }
    val data = memory.get(p, s)
    val (topics, newStack2) = newStack.popWords(num)
    val newLog = Log(data, topics)

    return context
        .updateCurrentCallContext(stack = newStack2)
        .copy(logs = context.logs + newLog)
}
