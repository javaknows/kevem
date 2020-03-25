import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.kevm.app.*
import org.kevm.web.Server
import org.kevm.rpc.module.EvmContext
import org.mockito.ArgumentMatchers.matches
import org.mockito.Mockito.reset
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.RuntimeException

internal class ServerStarterTest {

    private val commandLineParser = mock<CommandLineParser> {}
    private val evmContextCreator = mock<ServerEvmContextCreator> {}
    private val server = mock<Server> {}
    private val outStream = mock<PrintStream> {}
    private val errStream = mock<PrintStream> {}

    @BeforeEach
    internal fun setUp() {
        reset(commandLineParser, evmContextCreator, server, outStream, errStream)
    }

    @Test
    internal fun `error message is printed to err stream when parsing error is not null`() {
        val commandLineParser = mock<CommandLineParser> {
            on { parseCommandLine(any()) } doReturn CommandLineParseResult(null, "parse error")
        }

        val underTest = ServerStarter(commandLineParser, evmContextCreator, server, outStream, errStream)
        underTest.startServer(arrayOf("these are invalid arguments"))

        verify(errStream).println("Error parsing command line: parse error")
    }

    @Test
    internal fun `help is printed to out stream when help flag is set on command line`() {
        val commandLineParser = mock<CommandLineParser> {
            on { parseCommandLine(any()) } doReturn CommandLineParseResult(
                CommandLineArguments(help = true)
            )
            on { help() } doReturn "help message"
        }

        val underTest = ServerStarter(commandLineParser, evmContextCreator, server, outStream, errStream)
        underTest.startServer()

        verify(outStream).println("help message")
    }

    @Test
    internal fun `version is printed to out stream when version flag is set on command line`() {
        val commandLineParser = mock<CommandLineParser> {
            on { parseCommandLine(any()) } doReturn CommandLineParseResult(
                CommandLineArguments(version = true)
            )
        }

        val underTest = ServerStarter(commandLineParser, evmContextCreator, server, outStream, errStream)
        underTest.startServer()

        verify(outStream).println(any<String>())
    }

    @Test
    internal fun `err is printed to err output stream when context creation throws exception`() {
        val commandLineParser = mock<CommandLineParser> {
            on { parseCommandLine(any()) } doReturn CommandLineParseResult(
                CommandLineArguments(port = 9000, host = "example.com")
            )
        }
        val evmContextCreator = mock<ServerEvmContextCreator> {
            on { create(any()) } doThrow RuntimeException("context creation problem")
        }

        val underTest = ServerStarter(commandLineParser, evmContextCreator, server, outStream, errStream)
        underTest.startServer()

        verify(errStream).println("Error starting app: context creation problem")
    }

    @Test
    internal fun `err is printed to err output stream when starting server throws exception`() {
        val commandLineParser = mock<CommandLineParser> {
            on { parseCommandLine(any()) } doReturn CommandLineParseResult(
                CommandLineArguments(port = 9000, host = "example.com")
            )
        }
        val evmContext = mock<EvmContext>()
        val evmContextCreator = mock<ServerEvmContextCreator> {
            on { create(any()) } doReturn Pair(evmContext, "")
        }
        val server = mock<Server> {
            on { start(eq("example.com"), eq(9000), eq(true), eq(evmContext), any()) } doThrow RuntimeException("startup problem")
        }

        val underTest = ServerStarter(commandLineParser, evmContextCreator, server, outStream, errStream)
        underTest.startServer()

        verify(errStream).println("Error starting app: startup problem")
    }

    @Test
    internal fun `server is started started when no errors`() {
        val commandLineParser = mock<CommandLineParser> {
            on { parseCommandLine(any()) } doReturn CommandLineParseResult(
                CommandLineArguments(port = 9000, host = "example.com")
            )
        }
        val evmContext = mock<EvmContext>()
        val evmContextCreator = mock<ServerEvmContextCreator> {
            on { create(any()) } doReturn Pair(evmContext, "")
        }

        val underTest = ServerStarter(commandLineParser, evmContextCreator, server, outStream, errStream)
        underTest.startServer()

        verify(server).start(eq("example.com"), eq(9000), eq(true), eq(evmContext), any())
    }

    @Test
    internal fun `server exception with no cause and no message has unknown error message`() {
        val commandLineParser = mock<CommandLineParser> {
            on { parseCommandLine(any()) } doReturn CommandLineParseResult(
                CommandLineArguments(port = 9000, host = "example.com")
            )
        }
        val evmContextCreator = mock<ServerEvmContextCreator> {
            on { create(any()) } doThrow exceptionWithNoMessageAndNoCause()
        }

        val underTest = ServerStarter(commandLineParser, evmContextCreator, server, outStream, errStream)
        underTest.startServer()

        verify(errStream).println("Error starting app: unknown error. Try running with --verbose option for more information")
    }

    @Test
    internal fun `server exception with no message and cause with no message has unknown error message`() {
        val commandLineParser = mock<CommandLineParser> {
            on { parseCommandLine(any()) } doReturn CommandLineParseResult(
                CommandLineArguments(port = 9000, host = "example.com")
            )
        }
        val evmContextCreator = mock<ServerEvmContextCreator> {
            on { create(any()) } doThrow exceptionWithNoMessageAndCauseWithNoMessage()
        }

        val underTest = ServerStarter(commandLineParser, evmContextCreator, server, outStream, errStream)
        underTest.startServer()

        verify(errStream).println("Error starting app: unknown error. Try running with --verbose option for more information")
    }

    @Test
    internal fun `server exception with no message and cause with a message has the message from cause`() {
        val commandLineParser = mock<CommandLineParser> {
            on { parseCommandLine(any()) } doReturn CommandLineParseResult(
                CommandLineArguments(port = 9000, host = "example.com")
            )
        }
        val evmContextCreator = mock<ServerEvmContextCreator> {
            on { create(any()) } doThrow makeExceptionWithNoMessageAndCauseWithMessage()
        }

        val underTest = ServerStarter(commandLineParser, evmContextCreator, server, outStream, errStream)
        underTest.startServer()

        verify(errStream).println("Error starting app: foo message")
    }

    @Test
    internal fun `server exception with verbose flag set prints exception stack trace`() {
        val commandLineParser = mock<CommandLineParser> {
            on { parseCommandLine(any()) } doReturn CommandLineParseResult(
                CommandLineArguments(port = 9000, host = "example.com", verbose = true)
            )
        }
        val evmContextCreator = mock<ServerEvmContextCreator> {
            on { create(any()) } doThrow exceptionWithNoMessageAndNoCause()
        }

        val outputStream = ByteArrayOutputStream()
        val errStream = PrintStream(outputStream)

        val underTest = ServerStarter(commandLineParser, evmContextCreator, server, outStream, errStream)
        underTest.startServer()

        val errOutput = outputStream.toString()

        assertThat(errOutput)
            .contains("java.lang.RuntimeException")
            .contains("at org.kevm.app.ServerEvmContextCreator.create")
            .contains("at org.kevm.app.ServerStarter.startServer")
    }

    private fun exceptionWithNoMessageAndNoCause(): Exception {
        try {
            throw RuntimeException()
        } catch (e: Exception) {
            return e
        }
    }

    private fun exceptionWithNoMessageAndCauseWithNoMessage(): Exception {
        try {
            throw RuntimeException()
        } catch (e: Exception) {
            try {
                throw RuntimeException(null, e)
            } catch (e2: Exception) {
                return e2
            }
        }
    }

    private fun makeExceptionWithNoMessageAndCauseWithMessage(): Exception {
        try {
            throw RuntimeException()
        } catch (e: Exception) {
            try {
                throw RuntimeException("foo message", e)
            } catch (e2: Exception) {
                return e2
            }
        }
    }
}