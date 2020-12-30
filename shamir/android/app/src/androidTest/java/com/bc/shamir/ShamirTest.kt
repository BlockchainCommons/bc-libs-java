package com.bc.shamir

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShamirTest {

    @Test
    fun testShamirValidFlow() {
        // test Shamir#splitSecret
        val secret = hex2Bytes("00112233445566778899aabbccddeeff")
        val shards = Shamir.splitSecret(2, 3, secret) { len ->
            var b: Byte = 0
            val ret = ByteArray(len)
            for (i in 0 until len) {
                ret[i] = b
                b = (b + 17).toByte()
            }
            ret
        }

        arrayOf(
            "3dc2ff5b2a3b08193a2b1809a2b38091",
            "c7fe6b576e7f4c5df6e7d4c5e6f7c4d5",
            "d2bacc43a2b38091b9a89b8a2a3b0819"
        ).forEachIndexed { index, s ->
            assertEquals(index.toShort(), shards[index].index)
            assertEquals(s, bytes2Hex(shards[index].data))
        }

        // test Shamir#recoverSecret
        val recoveredShards = shards.toMutableList()
        recoveredShards.removeAt(1)
        val recoveredSecret = Shamir.recoverSecret(recoveredShards.toTypedArray())
        assertEquals(bytes2Hex(secret), bytes2Hex(recoveredSecret))

        recoveredShards.removeAt(0)
        val badRecoveredSecret = Shamir.recoverSecret(recoveredShards.toTypedArray())
        assertNotEquals(bytes2Hex(secret), bytes2Hex(badRecoveredSecret))

    }

    @Test
    fun testShamirError() {

        val randomFunc: (Int) -> ByteArray = { len -> byteArrayOf(0x00, 0x01) }
        val secret = hex2Bytes("00112233445566778899aabbccddeeff")

        assertThrows<ShamirException> { Shamir.splitSecret(-1, 5, secret, randomFunc) }
        assertThrows<ShamirException> { Shamir.splitSecret(3, -1, secret, randomFunc) }
        assertThrows<ShamirException> { Shamir.splitSecret(256, 4, secret, randomFunc) }
        assertThrows<ShamirException> { Shamir.splitSecret(2, 32267, null, randomFunc) }
        assertThrows<ShamirException> { Shamir.splitSecret(2, 3, null, randomFunc) }
        assertThrows<ShamirException> { Shamir.splitSecret(2, 3, secret, null) }

        assertThrows<ShamirException> { Shamir.recoverSecret(null) }
        assertThrows<ShamirException> { Shamir.recoverSecret(arrayOf()) }
        assertThrows<ShamirException> {
            Shamir.recoverSecret(
                arrayOf(
                    ShamirShard(
                        0,
                        hex2Bytes("3dc2ff5b2a3b08193a2b1809a2b38091")
                    ),
                    ShamirShard(
                        1,
                        hex2Bytes("c7fe6b576e7f4c5df6e7d4c5e6f")
                    ),
                    ShamirShard(2, hex2Bytes("d2ba"))
                )
            )
        }

    }

}