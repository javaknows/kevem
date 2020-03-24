package org.kevm.app

import org.kevm.web.Server

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
