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
            error != null -> err.println("Error parsing command line: $error")
            commandLine != null -> when {
                commandLine.help -> out.println(parser.help())
                commandLine.version -> out.println("KEVM") // https://github.com/wjsrobertson/kevm/issues/22
                else -> try {
                    val (evmContext, summary) = serverEvmContextCreator.create(commandLine)

                    server.start(commandLine.host, commandLine.port, true, evmContext) {
                        out.println(summary)
                    }
                } catch (e: Exception) {
                    val message = resolveExceptionMessage(e)

                    err.println("Error starting app: $message")

                    if (commandLine.verbose) {
                        e.printStackTrace(err)
                    }
                }
            }
        }
    }

    private fun resolveExceptionMessage(e: Exception): String? =
        when {
            e.message != null -> e.message
            e.cause != null && e.cause?.message != null -> e.cause?.message
            else -> "unknown error. Try running with --verbose option for more information"
        }
}

