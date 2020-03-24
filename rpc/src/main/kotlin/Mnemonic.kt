package org.kevm.rpc

import org.kevm.common.KevmException
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import org.web3j.crypto.MnemonicUtils

/*
https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki
https://github.com/satoshilabs/slips/blob/master/slip-0044.md
 */
object Mnemonic {
    const val path = "m/44'/60'/0'/0"

    fun keyPairsFromMnemonic(mnemonic: String, numAccounts: Int): List<KeyPair> {
        if (!MnemonicUtils.validateMnemonic(mnemonic)) {
            throw KevmException("invalid mnemonic: $mnemonic")
        }

        val masterKey = Bip32ECKeyPair.generateKeyPair(
            MnemonicUtils.generateSeed(mnemonic, null)
        )

        val (purpose, coinType, account, change) = path.replace("'", "").split("/").drop(1).map(String::toInt)

        return (0 until numAccounts).map { addressIndex ->
            val path = intArrayOf(
                purpose or Bip32ECKeyPair.HARDENED_BIT,
                coinType or Bip32ECKeyPair.HARDENED_BIT,
                account or Bip32ECKeyPair.HARDENED_BIT,
                change,
                addressIndex
            )
            val pair = Bip32ECKeyPair.deriveKeyPair(masterKey, path)
            val address = Credentials.create(pair.privateKey.toString(16)).address
            val privateKey = "0x" + pair.privateKey.toString(16)

            KeyPair(address, privateKey)
        }
    }

}

