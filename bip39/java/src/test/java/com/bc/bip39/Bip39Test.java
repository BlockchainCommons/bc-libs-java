package com.bc.bip39;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.bc.bip39.util.TestUtils.assertThrows;
import static com.bc.bip39.util.TestUtils.hex2Bytes;
import static com.bc.bip39.util.TestUtils.slice;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class Bip39Test {

    @Test
    public void testBIP39Word() {
        assertEquals("abandon", Bip39.word(0));
        assertEquals("leg", Bip39.word(1018));
        assertEquals("length", Bip39.word(1024));
        assertEquals("zoo", Bip39.word(2047));
        assertThrows("Bip39.word(2048)", Bip39Exception.class, () -> Bip39.word(2048));
    }

    @Test
    public void testBIP39Index() {
        assertEquals(0, Bip39.index("abandon"));
        assertEquals(1018, Bip39.index("leg"));
        assertEquals(1024, Bip39.index("length"));
        assertEquals(2047, Bip39.index("zoo"));
        assertThrows("Bip39.index(\"aaa\")", Bip39Exception.class, () -> Bip39.index("aaa"));
        assertThrows("Bip39.index(\"zzz\")", Bip39Exception.class, () -> Bip39.index("zzz"));
        assertThrows("Bip39.index(\"123\")", Bip39Exception.class, () -> Bip39.index("123"));
        assertThrows("Bip39.index(\"ley\")", Bip39Exception.class, () -> Bip39.index("ley"));
        assertThrows("Bip39.index(\"lengthz\")",
                     Bip39Exception.class,
                     () -> Bip39.index("lengthz"));
        assertThrows("Bip39.index(\"zoot\")", Bip39Exception.class, () -> Bip39.index("zoot"));
    }

    @Test
    public void testSeedFromString() {
        String rolls = "123456";
        byte[] expectedSecret = hex2Bytes("8d969eef6ecad3c29a3a629280e686cf");
        byte[] secret = Bip39.seed(rolls);
        assertArrayEquals(expectedSecret, slice(secret, 16));
    }

    @Test
    public void testEncode() {
        assertEquals("rival hurdle address inspire tenant alone",
                     Bip39.encode(hex2Bytes("baadf00dbaadf00d")));
        assertEquals("rival hurdle address inspire tenant almost turkey safe asset step lab boy",
                     Bip39.encode(hex2Bytes("baadf00dbaadf00dbaadf00dbaadf00d")));
        assertThrows("Bip39.encode(hex2Bytes(\"baadf00dbaadf00dbaadf00dbaadf00dff\")",
                     Bip39Exception.class,
                     () -> Bip39.encode(hex2Bytes("baadf00dbaadf00dbaadf00dbaadf00dff")));
        assertEquals("legal winner thank year wave sausage worth useful legal winner thank yellow",
                     Bip39.encode(hex2Bytes("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f")));
    }

    @Test
    public void testDecode() {
        assertArrayEquals(hex2Bytes("baadf00dbaadf00d"),
                          Bip39.decode("rival hurdle address inspire tenant alone"));
        assertArrayEquals(hex2Bytes("baadf00dbaadf00dbaadf00dbaadf00d"),
                          Bip39.decode(
                                  "rival hurdle address inspire tenant almost turkey safe asset step lab boy"));
        assertArrayEquals(hex2Bytes("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f"),
                          Bip39.decode(
                                  "legal winner thank year wave sausage worth useful legal winner thank yellow"));
    }
}
