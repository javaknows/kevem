package org.kevm.app

import org.kevm.web.Server
import java.io.PrintStream

class ServerStarter(
    private val parser: CommandLineParser,
    private val serverEvmContextCreator: ServerEvmContextCreator,
    private val server: Server,
    private val out: PrintStream,
    private val err: PrintStream
) {
    fun startServer(args: Array<String> = emptyArray()) = with(parser.parseCommandLine(args)) {
        when {
            error != null -> {
                err.println("Error parsing command line: $error")
            }
            commandLine != null && commandLine.help -> {
                out.println(parser.help())
            }
            commandLine != null && commandLine.version -> {
                out.println("KEVM") // https://github.com/wjsrobertson/kevm/issues/22
            }
            commandLine != null -> {
                try {
                    val (evmContext, summary) = serverEvmContextCreator.create(commandLine)

                    server.start(commandLine.port, true, evmContext) {
                        out.println(summary)
                    }
                } catch (e: Exception) {
                    err.println("Error starting app: ${e.message}")
                }
            }
        }
    }
}

