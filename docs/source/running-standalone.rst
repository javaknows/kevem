Running As A Standalone Application
###################################

.. _running-standalone:

Kevem can run as a standalone JSON-RPC server by running the `kevem-app` executable fat jar:

::

    java -jar kevem-app-0.1.0.jar

You'll need Java 8 or later.

By default Kevem will listen on port 8545 of localhost and generate 10 unlocked accounts with 100 ETH each.

You can use different values for the wallet mnemonic, host and port etc.

::

    java -jar kevem-app-0.1.0.jar -p 8888 -n 2 -m "stay jeans limb improve struggle return predict flower assume giraffe mother spring"


All command line arguments
==========================

+------+-----------------------------+--------------------------------------------------------------------+
| flag | long flag                   | description                                                        |
+======+=============================+====================================================================+
| -p   | --port <arg>                |  port web RPC service listens on (8545)                            |
+------+-----------------------------+--------------------------------------------------------------------+
| -h   | --host <arg>                |  hostname web RPC service listens on (localhost)                   |
+------+-----------------------------+--------------------------------------------------------------------+
| -n   | --numAccounts <arg>         |  number of accounts to create when using (10)                      |
+------+-----------------------------+--------------------------------------------------------------------+
| -m   | --mnemonic <arg>            |  bip39 mnemonic phrase for account generation (hard-coded default) |
+------+-----------------------------+--------------------------------------------------------------------+
| -e   | --defaultBalanceEther <arg> |  balance for each generated account (100)                          |
+------+-----------------------------+--------------------------------------------------------------------+
| -g   | --gasPrice <arg>            |  block gas price in wei                                            |
+------+-----------------------------+--------------------------------------------------------------------+
| -l   | --gasLimit <arg>            |  block gas limit in wei                                            |
+------+-----------------------------+--------------------------------------------------------------------+
| -c   | --chainId <arg>             |  chain ID (0)                                                      |
+------+-----------------------------+--------------------------------------------------------------------+
| -i   | --networkId <arg>           |  network ID (1)                                                    |
+------+-----------------------------+--------------------------------------------------------------------+
| -v   | --verbose                   |  print extra output including stack traces for startup errors      |
+------+-----------------------------+--------------------------------------------------------------------+
|      | --help                      |  display help then exit                                            |
+------+-----------------------------+--------------------------------------------------------------------+
|      | --version                   |  display version then exit                                         |
+------+-----------------------------+--------------------------------------------------------------------+

Output at Startup
=================

Kevem will print out available local accounts, their private keys and balances in a format a lot like Ganache

::

    $ java -jar ./app/build/libs/kevem-app-0.1.0.jar -p 8888 -n 2 -m "stay jeans limb improve struggle return predict flower assume giraffe mother spring"
    Kevem

    Available Accounts
    ==================
    (0) 0x4e7d932c0f12cfe14295b86824b37bb1062bc29e (100 ETH)
    (1) 0x6e69847df277bb9c3e88f170be883d4a6195f180 (100 ETH)

    Private Keys
    ============
    (0) 0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395
    (1) 0xc2a4b649d0516ea346b41e6524b4cc43d87f14f9eb33a63469bba75a10842147

    Wallet
    ======
    Mnemonic: stay jeans limb improve struggle return predict flower assume giraffe mother spring
    Path:     m/44'/60'/0'/0/{account_index}

    Gas Price
    =========
    20000000000

    Gas Limit
    =========
    1000000000000000000000000000000

    Listening on localhost:8888