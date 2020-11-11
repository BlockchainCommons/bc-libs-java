package com.bc.sskr

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.RuntimeException

@RunWith(AndroidJUnit4::class)
class SSKRTest {

    @Test
    fun testSSKRValidFlow() {
        // check SSKR#countShares
        val groupThreshold = 2
        val groups = arrayOf(
            SSKRGroupDescriptor(2, 3),
            SSKRGroupDescriptor(3, 5)
        )
        val expectedShareCount = 8
        val shareCount = SSKR.countShares(groupThreshold, groups)
        Assert.assertEquals(expectedShareCount, shareCount)

        // check SSKR#generate
        val secret = hex2Bytes("00112233445566778899aabbccddeeff")
        val shareGroup = SSKR.generate(groupThreshold, groups, secret)
        { len ->
            var b: Byte = 0
            val ret = ByteArray(len)
            for (i in 0 until len) {
                ret[i] = b
                b = (b + 17).toByte()
            }
            ret
        }

        val expectedShareData =
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
        for (i in shareGroup.indices) {
            val shares = shareGroup[i]
            for (j in shares.indices) {
                Assert.assertEquals(
                    expectedShareData[i][j],
                    bytes2Hex(shareGroup[i][j].data)
                )
            }
        }

        // check SSKR#combine
        val recoveredShares = shareGroup.flatten().toMutableList()
            .filterIndexed { index, _ -> index !in arrayOf(1, 3, 6) }
        val recoveredSecret = SSKR.combine(recoveredShares.toTypedArray())
        Assert.assertEquals(
            bytes2Hex(secret),
            bytes2Hex(recoveredSecret)
        )

        val invalidRecoveredShares =
            recoveredShares.toMutableList().filterIndexed { index, _ -> index != 3 }
        try {
            SSKR.combine(invalidRecoveredShares.toTypedArray())
            throw RuntimeException("check SSKR#combine failed")
        } catch (ignore: SSKRException) {
        }
    }

    @Test
    fun testSSKRError() {

        fun internalTest(func: () -> Unit) {
            try {
                func()
                throw RuntimeException("test is failed")
            } catch (ignore: SSKRException) {
            }
        }

        val groups = arrayOf(SSKRGroupDescriptor(2, 3))
        val secret = hex2Bytes("00112233445566778899aabbccddeeff")
        val randomFunc: (Int) -> ByteArray = { byteArrayOf(0x01, 0x02) }

        // check SSKR#countShares
        internalTest { SSKR.countShares(2, null) }
        internalTest { SSKR.countShares(-1, groups) }

        // check SSKR#generate
        internalTest { SSKR.generate(-1, groups, secret, randomFunc) }
        internalTest { SSKR.generate(2, null, secret, randomFunc) }
        internalTest { SSKR.generate(2, groups, byteArrayOf(0x7F), randomFunc) }
        internalTest { SSKR.generate(2, groups, null, randomFunc) }
        internalTest { SSKR.generate(2, groups, secret, null) }

        // check SSKR#combine
        internalTest { SSKR.combine(null) }
        internalTest {
            SSKR.combine(
                arrayOf(
                    SSKRShare(byteArrayOf(0x00, 0x01, 0x02)),
                    SSKRShare(byteArrayOf())
                )
            )
        }
    }
}