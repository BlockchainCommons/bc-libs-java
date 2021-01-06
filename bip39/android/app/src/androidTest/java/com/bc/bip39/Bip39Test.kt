package com.bc.bip39

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bc.bip39.util.assertThrows
import com.bc.bip39.util.hex2Bytes
import com.bc.bip39.util.slice
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Bip39Test {

    @Test
    fun testBIP39Word() {
        assertEquals("abandon", Bip39.word(0))
        assertEquals("leg", Bip39.word(1018))
        assertEquals("length", Bip39.word(1024))
        assertEquals("zoo", Bip39.word(2047))
        assertThrows<Bip39Exception>("Bip39.word(2048)") { Bip39.word(2048) }
    }

    @Test
    fun testBIP39Index() {
        assertEquals(0, Bip39.index("abandon").toLong())
        assertEquals(1018, Bip39.index("leg").toLong())
        assertEquals(1024, Bip39.index("length").toLong())
        assertEquals(2047, Bip39.index("zoo").toLong())
        assertThrows<Bip39Exception>("Bip39.index(\"aaa\")") { Bip39.index("aaa") }
        assertThrows<Bip39Exception>("Bip39.index(\"zzz\")") { Bip39.index("zzz") }
        assertThrows<Bip39Exception>("Bip39.index(\"123\")") { Bip39.index("123") }
        assertThrows<Bip39Exception>("Bip39.index(\"ley\")") { Bip39.index("ley") }
        assertThrows<Bip39Exception>("Bip39.index(\"lengthz\")") { Bip39.index("lengthz") }
        assertThrows<Bip39Exception>("Bip39.index(\"zoot\")") { Bip39.index("zoot") }
    }

    @Test
    fun testSeedFromString() {
        val rolls = "123456"
        val expectedSecret = "8d969eef6ecad3c29a3a629280e686cf".hex2Bytes()
        val secret = Bip39.seed(rolls)
        assertArrayEquals(expectedSecret, slice(secret, 16))
    }

    @Test
    fun testEncode() {
        assertEquals(
            "rival hurdle address inspire tenant alone",
            Bip39.encode("baadf00dbaadf00d".hex2Bytes())
        )

        assertEquals(
            "rival hurdle address inspire tenant almost turkey safe asset step lab boy",
            Bip39.encode("baadf00dbaadf00dbaadf00dbaadf00d".hex2Bytes())
        )

        assertThrows<Bip39Exception>(
            "Bip39.encode(\"baadf00dbaadf00dbaadf00dbaadf00dff\".hex2Bytes())"
        ) { Bip39.encode("baadf00dbaadf00dbaadf00dbaadf00dff".hex2Bytes()) }

        assertEquals(
            "legal winner thank year wave sausage worth useful legal winner thank yellow",
            Bip39.encode("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f".hex2Bytes())
        )
    }

    @Test
    fun testDecode() {
        assertArrayEquals(
            "baadf00dbaadf00d".hex2Bytes(),
            Bip39.decode("rival hurdle address inspire tenant alone")
        )

        assertArrayEquals(
            "baadf00dbaadf00dbaadf00dbaadf00d".hex2Bytes(),
            Bip39.decode(
                "rival hurdle address inspire tenant almost turkey safe asset step lab boy"
            )
        )

        assertArrayEquals(
            "7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f".hex2Bytes(),
            Bip39.decode(
                "legal winner thank year wave sausage worth useful legal winner thank yellow"
            )
        )
    }

}