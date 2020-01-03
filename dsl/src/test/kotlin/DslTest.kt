package org.kevm.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.kevm.common.KevmException

import org.kevm.evm.model.Address
import java.lang.Exception
import java.math.BigInteger
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class DslTest {

    @Test
    fun `can create account with balance`() {
        val accounts = kevm {
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
        val accounts = kevm {
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
    fun `can set address when setting primary key if it matches`() {
        val accounts = kevm {
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
        val exception: KevmException = assertThrows {
            kevm {
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
        val exception: KevmException = assertThrows {
            kevm {
                account {
                    balance = eth(1)
                }
            }
        }

        assertThat(exception.message).contains("Cannot create account without providing address or private key")
    }

    @Test
    fun `config values can be set`() {
        val kevm = kevm(
            config = Config(
                coinbase = "0xABCDEF",
                blockGasLimit = BigInteger.TEN
            )
        )

        assertThat(kevm.appConfig.coinbase).isEqualTo("0xABCDEF")
        assertThat(kevm.appConfig.blockGasLimit).isEqualTo(BigInteger.TEN)
    }

    @Test
    fun `clock can be set`() {
        val fixedClock = Clock.fixed(Instant.parse("2015-06-30T03:26:28.00Z"), ZoneId.systemDefault())

        val kevm = kevm(
            clock = fixedClock
        )

        assertThat(kevm.clock).isEqualTo(fixedClock)
    }

    @Test
    fun `can create usable web3j`() {
        val web3j = kevm(
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
}