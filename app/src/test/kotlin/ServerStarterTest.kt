import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.kevm.app.*
import org.kevm.web.Server
import org.kevm.web.module.EvmContext
import org.mockito.Mockito.reset
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
            on { start(eq(9000), eq(true), eq(evmContext), any()) } doThrow RuntimeException("startup problem")
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

        verify(server).start(eq(9000), eq(true), eq(evmContext), any())
    }

}