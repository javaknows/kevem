Kotlin DSL
##########

.. _kotlin-dsl:

Kevem supports a DSL for configuring the EVM instance. This lets you set up accounts and balances.

Prerequisites
=============

Ensure `kevem-dsl-nodep.jar` is on your classpath along with `kotlin-stdlib` plus `web3j` version `5.3.x` and its transitive dependencies.

Account Setup
=============

.. hint::

    Accounts will be created with 100 ETH if no amount is specified

mnemonicAccounts method
-----------------------

Use the `mnemonicAccounts` method to create a set of accounts based on a bip39 mnemonic phrase. You can optionally specify specify the

::

    kevem {
            mnemonicAccounts {
                mnemonic = "stay jeans limb improve struggle return predict flower assume giraffe mother spring",
                balance = eth(1000) // optional - defaults to 100,
                numAccounts = 10    // optional - defaults to 1
            }
        }

account method
--------------

**Create Account By Primary Key**

::

    kevem {
            account {
                privateKey = "0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395",
                balance = eth(1000) // optional - defaults to 100
            }
        }

**Create Account By Address**

::

    kevem {
            account {
                address = "0x4e7d932c0f12cfe14295b86824b37bb1062bc29e",
                balance = eth(1000) // optional - defaults to 100
            }
        }

