package org.kevem.app

import org.kevem.web.Server

fun main(args: Array<String>) {
    createServerStarter().startServer(args)
}

private fun createServerStarter() =
    ServerStarter(
        ApacheCommonsCliCommandLineParser(),
        ServerEvmContextCreator(
            StartupSummariser(), CommandLineAccountsCreator(), CommandLineEvmContextBuilder()
        ),
        Server(),
        System.out,
        System.err
    )
