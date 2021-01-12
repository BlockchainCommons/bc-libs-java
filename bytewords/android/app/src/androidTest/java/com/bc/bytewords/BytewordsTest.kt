package com.bc.bytewords

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bc.bytewords.util.assertThrows
import com.bc.bytewords.util.hex2Bytes
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BytewordsTest {

    @Test
    fun testEncodeDecode() {
        val bytes1 = byteArrayOf(0, 1, 2, 128.toByte(), 255.toByte())
        val standardEncoded1 = "able acid also lava zoom jade need echo taxi"
        val uriEncoded1 = "able-acid-also-lava-zoom-jade-need-echo-taxi"
        val minimalEncoded1 = "aeadaolazmjendeoti"
        assertEquals(standardEncoded1, Bytewords.encode(BytewordsStyle.STANDARD, bytes1))
        assertEquals(uriEncoded1, Bytewords.encode(BytewordsStyle.URI, bytes1))
        assertEquals(minimalEncoded1, Bytewords.encode(BytewordsStyle.MINIMAL, bytes1))
        assertArrayEquals(bytes1, Bytewords.decode(BytewordsStyle.STANDARD, standardEncoded1))
        assertArrayEquals(bytes1, Bytewords.decode(BytewordsStyle.URI, uriEncoded1))
        assertArrayEquals(bytes1, Bytewords.decode(BytewordsStyle.MINIMAL, minimalEncoded1))

        val bytes2 =
            ("F5D714C6F1EB453BD1CDA512969E7487E5D4139F1125EFF0FD0B6DBF25F22678DF299CBDF2FE93CC42A" +
                    "3D8AFBF48A936203C90E6D289B8C52171580E9D1FB12E0173CD45E19641EB3A9041F0854571F" +
                    "73F35F2A5A0901A0D4FED85475245FEA58A295518").hex2Bytes()
        val standardEncoded2 =
            "yank toys bulb skew when warm free fair tent swan open brag mint noon jury list " +
                    "view tiny brew note body data webs what zinc bald join runs data whiz " +
                    "days keys user diet news ruby whiz zone menu surf flew omit trip pose " +
                    "runs fund part even crux fern math visa tied loud redo silk curl jugs " +
                    "hard beta next cost puma drum acid junk swan free very mint flap warm " +
                    "fact math flap what limp free jugs yell fish epic whiz open numb math " +
                    "city belt glow wave limp fuel grim free zone open love diet gyro cats " +
                    "fizz holy city puff"
        val minimalEncoded2 =
            "yktsbbswwnwmfefrttsnonbgmtnnjyltvwtybwnebydawswtzcbdjnrsdawzdsksurdtnsrywzzemu" +
                    "sffwottppersfdptencxfnmhvatdldroskcljshdbantctpadmadjksnfevymtfpwmftmh" +
                    "fpwtlpfejsylfhecwzonnbmhcybtgwwelpflgmfezeonledtgocsfzhycypf"
        assertEquals(standardEncoded2, Bytewords.encode(BytewordsStyle.STANDARD, bytes2))
        assertEquals(minimalEncoded2, Bytewords.encode(BytewordsStyle.MINIMAL, bytes2))
        assertArrayEquals(bytes2, Bytewords.decode(BytewordsStyle.STANDARD, standardEncoded2))
        assertArrayEquals(bytes2, Bytewords.decode(BytewordsStyle.MINIMAL, minimalEncoded2))
    }

    @Test
    fun testWord() {
        assertEquals("able", Bytewords.word(0))
        assertEquals("aunt", Bytewords.word(7))
        assertEquals("duty", Bytewords.word(48))
        assertEquals("easy", Bytewords.word(50))
        assertEquals("zone", Bytewords.word(254))
        assertEquals("zoom", Bytewords.word(255))
        assertThrows<BytewordsException>("Bytewords.word(-1)") { Bytewords.word(-1) }
        assertThrows<BytewordsException>() { Bytewords.word(256) }
    }

    @Test
    fun testError() {
        assertThrows<BytewordsException>("Bytewords.encode(null, new byte[]{})") {
            Bytewords.encode(
                null,
                byteArrayOf()
            )
        }
        assertThrows<BytewordsException>("Bytewords.decode(null, \"\")") {
            Bytewords.decode(
                null,
                ""
            )
        }
        assertThrows<BytewordsException>("Bytewords.encode(BytewordsStyle.STANDARD, null)") {
            Bytewords.encode(
                BytewordsStyle.STANDARD,
                null
            )
        }
        assertThrows<BytewordsException>("Bytewords.decode(BytewordsStyle.URI, \"\")") {
            Bytewords.decode(
                BytewordsStyle.URI,
                ""
            )
        }
        assertThrows<BytewordsException>("Bytewords.decode(BytewordsStyle.STANDARD, \"aeadaolazmjendeoti\"") {
            Bytewords.decode(
                BytewordsStyle.STANDARD,
                "aeadaolazmjendeoti"
            )
        }
    }

}