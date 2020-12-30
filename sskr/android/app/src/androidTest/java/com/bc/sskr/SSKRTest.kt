package com.bc.sskr

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SSKRTest {

    @Test
    fun testSSKRValidFlow() {
        // check SSKR#countShards
        val groupThreshold = 2
        val groups = arrayOf(
            SSKRGroupDescriptor(2, 3),
            SSKRGroupDescriptor(3, 5)
        )
        val expectedShardsCount = 8
        val shardsCount = SSKR.countShards(groupThreshold, groups)
        assertEquals(expectedShardsCount, shardsCount)

        // check SSKR#generate
        val secret = hex2Bytes("00112233445566778899aabbccddeeff")
        val shardGroup = SSKR.generate(groupThreshold, groups, secret)
        { len ->
            var b: Byte = 0
            val ret = ByteArray(len)
            for (i in 0 until len) {
                ret[i] = b
                b = (b + 17).toByte()
            }
            ret
        }

        val expectedShardData =
            arrayOf(
                arrayOf(
                    "1100110100bae4b1dda4b58697b3a291802c3d0e1f",
                    "11001101016c0158178e9facbdcddceffe06172435",
                    "11001101020d357852f0e1d2c34f5e6d7c78695a4b"
                ), arrayOf(
                    "110011120000112233445566778899aabbccddeeff",
                    "1100111201f1a1a2262a3b08193a2b1809a2b38091",
                    "1100111202f0a55798b3a291808a9ba8b93b2a1908",
                    "11001112030115d78dddccffee38291a0b55447766",
                    "1100111204df736b4c1d0c3f2e637241509584b7a6"
                )
            )
        for (i in shardGroup.indices) {
            val shards = shardGroup[i]
            for (j in shards.indices) {
                assertEquals(
                    expectedShardData[i][j],
                    bytes2Hex(shardGroup[i][j].data)
                )
            }
        }

        // check SSKR#combine
        val recoveredShards = shardGroup.flatten().toMutableList()
            .filterIndexed { index, _ -> index !in arrayOf(1, 3, 6) }
        val recoveredSecret = SSKR.combine(recoveredShards.toTypedArray())
        assertEquals(
            bytes2Hex(secret),
            bytes2Hex(recoveredSecret)
        )

        val invalidRecoveredShards =
            recoveredShards.toMutableList().filterIndexed { index, _ -> index != 3 }
        try {
            SSKR.combine(invalidRecoveredShards.toTypedArray())
            throw RuntimeException("check SSKR#combine failed")
        } catch (ignore: SSKRException) {
        }
    }

    @Test
    fun testSSKRError() {

        val groups = arrayOf(SSKRGroupDescriptor(2, 3))
        val secret = hex2Bytes("00112233445566778899aabbccddeeff")
        val randomFunc: (Int) -> ByteArray = { byteArrayOf(0x01, 0x02) }

        // check SSKR#countShards
        assertThrows<SSKRException> { SSKR.countShards(2, null) }
        assertThrows<SSKRException> { SSKR.countShards(-1, groups) }

        // check SSKR#generate
        assertThrows<SSKRException> { SSKR.generate(-1, groups, secret, randomFunc) }
        assertThrows<SSKRException> { SSKR.generate(2, null, secret, randomFunc) }
        assertThrows<SSKRException> { SSKR.generate(2, groups, byteArrayOf(0x7F), randomFunc) }
        assertThrows<SSKRException> { SSKR.generate(2, groups, null, randomFunc) }
        assertThrows<SSKRException> { SSKR.generate(2, groups, secret, null) }

        // check SSKR#combine
        assertThrows<SSKRException> { SSKR.combine(null) }
        assertThrows<SSKRException> {
            SSKR.combine(
                arrayOf(
                    SSKRShard(byteArrayOf(0x00, 0x01, 0x02)),
                    SSKRShard(byteArrayOf())
                )
            )
        }
    }
}