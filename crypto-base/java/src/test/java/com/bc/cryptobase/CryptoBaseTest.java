package com.bc.cryptobase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.bc.cryptobase.TestUtils.bytes2Hex;
import static com.bc.cryptobase.TestUtils.hex2Bytes;
import static com.bc.cryptobase.TestUtils.removeLeadingZeros;
import static com.bc.cryptobase.TestUtils.toBigEndian;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class CryptoBaseTest {

    @Test
    public void testSHA() {
        byte[] message = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq".getBytes();
        byte[] digest256 = CryptoBase.sha256(message);
        assertEquals("248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1",
                     bytes2Hex(digest256));

        byte[] digest512 = CryptoBase.sha512(message);
        assertEquals(
                "204a8fc6dda82f0a0ced7beb8e08a41657c16ef468b228a8279be331a703c33596fd15c13b1b07f9aa1d3bea57789ca031ad85c7a71dd70354ec631238ca3445",
                bytes2Hex(digest512));
    }

    @Test
    public void testHMACSHA() {
        byte[] key = hex2Bytes("0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b");
        byte[] message = "Hi There".getBytes();
        byte[] hmac256 = CryptoBase.hmacSHA256(key, message);
        assertEquals("b0344c61d8db38535ca8afceaf0bf12b881dc200c9833da726e9376c2e32cff7",
                     bytes2Hex(hmac256));

        byte[] hmac512 = CryptoBase.hmacSHA512(key, message);
        assertEquals(
                "87aa7cdea5ef619d4ff0b4241a1d6cb02379f4e2ce4ec2787ad0b30545e17cdedaa833b7d6b8a702038b274eaea3f4e4be9d914eeb61f1702e696c203a126854",
                bytes2Hex(hmac512));
    }

    @Test
    public void testPBKDF2HMACSHA256() {
        byte[] pass = "password".getBytes();
        byte[] salt = "salt".getBytes();
        long iteration = 1L;
        int keyLength = 32;
        byte[] key = CryptoBase.pbkdf2HMACSHA256(pass, salt, iteration, keyLength);
        assertEquals("120fb6cffcf8b32c43e7225256c4f837a86548c92ccc35480805987cb70be17b",
                     bytes2Hex(key));
    }

    @Test
    public void testCRC32() {
        byte[] bytes = "Hello, world!".getBytes();
        long checksum = CryptoBase.crc32(bytes);
        assertEquals("ebe6c6e6", bytes2Hex(removeLeadingZeros(toBigEndian(checksum))));

        long checksumN = CryptoBase.crc32n(bytes);
        assertEquals("e6c6e6eb", bytes2Hex(removeLeadingZeros(toBigEndian(checksumN))));
    }

}
