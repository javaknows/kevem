package org.kevem.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.kevem.common.KevemException

import org.kevem.evm.model.Address
import org.kevem.evm.toByteList
import org.kevem.rpc.LocalAccount
import java.math.BigInteger
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class DslTest {

    @Test
    fun `can create account with balance`() {
        val accounts = kevem {
            account {
                balance = eth(1)
                address = "0xADD7E55"
            }
        }.accounts

        assertThat(
            accounts.balanceOf(Address("0xADD7E55"))
        ).isEqualTo("1000000000000000000")
    }

    @Test
    fun `can create account from primary key`() {
        val accounts = kevem {
            account {
                balance = eth(1)
                privateKey = "0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395"
            }
        }.accounts

        assertThat(
            accounts.balanceOf(Address("0x4e7d932c0f12cfe14295b86824b37bb1062bc29e"))
        ).isEqualTo("1000000000000000000")
    }

    @Test
    fun `created account has default balance of 100 ETH`() {
        val accounts = kevem {
            account {
                address = "0xADD7E55"
            }
        }.accounts

        assertThat(
            accounts.balanceOf(Address("0xADD7E55"))
        ).isEqualTo("100000000000000000000")
    }

    @Test
    fun `can set address when setting primary key if it matches`() {
        val accounts = kevem {
            account {
                balance = eth(1)
                privateKey = "0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395"
                address = "0x4e7d932c0f12cfe14295b86824b37bb1062bc29e"
            }
        }.accounts

        assertThat(
            accounts.balanceOf(Address("0x4e7d932c0f12cfe14295b86824b37bb1062bc29e"))
        ).isEqualTo("1000000000000000000")
    }

    @Test
    fun `setting address fails when setting private key if they do not match`() {
        val exception: KevemException = assertThrows {
            kevem {
                account {
                    balance = eth(1)
                    privateKey = "0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395"
                    address = "0x11111111111111111111111111111111111111111"
                }
            }
        }

        assertThat(exception.message).contains("Address must match private key")
    }

    @Test
    fun `cannot create account without providing address or private key`() {
        val exception: KevemException = assertThrows {
            kevem {
                account {
                    balance = eth(1)
                }
            }
        }

        assertThat(exception.message).contains("Cannot create account without providing address or private key")
    }

    @Test
    fun `config values can be set`() {
        val kevem = kevem(
            config = Config(
                coinbase = "0xABCDEF",
                blockGasLimit = BigInteger.TEN
            )
        )

        assertThat(kevem.appConfig.coinbase).isEqualTo("0xABCDEF")
        assertThat(kevem.appConfig.blockGasLimit).isEqualTo(BigInteger.TEN)
    }

    @Test
    fun `clock can be set`() {
        val fixedClock = Clock.fixed(Instant.parse("2015-06-30T03:26:28.00Z"), ZoneId.systemDefault())

        val kevem = kevem(
            clock = fixedClock
        )

        assertThat(kevem.clock).isEqualTo(fixedClock)
    }

    @Test
    fun `can create usable web3j`() {
        val web3j = kevem(
            config = Config(
                coinbase = "0x123456"
            )
        ) {
            account {
                balance = eth(1)
                privateKey = "0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395"
            }
        }.toWeb3j()

        val coinbase = web3j.ethCoinbase().send().address

        assertThat(coinbase).isEqualTo("0x123456")
    }

    @Test
    fun `can create accounts using valid mnemonic`() {
        val evm = kevem {
            mnemonicAccounts {
                mnemonic = "stay jeans limb improve struggle return predict flower assume giraffe mother spring"
                balance = eth(3)
                numAccounts = 2
            }
        }

        assertThat(evm.accounts.list()).hasSize(2)

        assertThat(
            evm.accounts.balanceOf(Address("0x4e7d932c0f12cfe14295b86824b37bb1062bc29e"))
        ).isEqualTo("3000000000000000000")

        assertThat(
            evm.accounts.balanceOf(Address("0x6e69847df277bb9c3e88f170be883d4a6195f180"))
        ).isEqualTo("3000000000000000000")

        assertThat(evm.localAccounts.toSet()).isEqualTo(
            setOf(
                LocalAccount(
                    Address("0x4e7d932c0f12cfe14295b86824b37bb1062bc29e"),
                    toByteList("0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395"),
                    false
                ),
                LocalAccount(
                    Address("0x6e69847df277bb9c3e88f170be883d4a6195f180"),
                    toByteList("0xc2a4b649d0516ea346b41e6524b4cc43d87f14f9eb33a63469bba75a10842147"),
                    false
                )
            )
        )
    }

    @Test
    fun `mnemonic account has default balance of 100 ETH`() {
        val evm = kevem {
            mnemonicAccounts {
                mnemonic = "stay jeans limb improve struggle return predict flower assume giraffe mother spring"
                numAccounts = 1
            }
        }

        assertThat(
            evm.accounts.balanceOf(Address("0x4e7d932c0f12cfe14295b86824b37bb1062bc29e"))
        ).isEqualTo("100000000000000000000")
    }

    @Test
    fun `one mnemonic account is created by default`() {
        val evm = kevem {
            mnemonicAccounts {
                mnemonic = "stay jeans limb improve struggle return predict flower assume giraffe mother spring"
            }
        }

        assertThat(evm.accounts.list()).hasSize(1)
    }

    @Test
    fun `invalid mnemonic creates exception when creating mnemonic account`() {
        val exception: KevemException = assertThrows {
            kevem {
                mnemonicAccounts {
                    mnemonic = "INVALID stay jeans limb improve struggle return predict flower assume giraffe mother spring"
                }
            }
        }

        assertThat(exception.message).contains("invalid mnemonic")
    }

}