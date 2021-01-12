package com.bc.bytewords;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.bc.bytewords.util.TestUtils.assertThrows;
import static com.bc.bytewords.util.TestUtils.hex2Bytes;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class BytewordsTest {

    @Test
    public void testEncodeDecode() {
        byte[] bytes1 = new byte[]{0, 1, 2, (byte) 128, (byte) 255};
        String standardEncoded1 = "able acid also lava zoom jade need echo taxi";
        String uriEncoded1 = "able-acid-also-lava-zoom-jade-need-echo-taxi";
        String minimalEncoded1 = "aeadaolazmjendeoti";

        assertEquals(standardEncoded1, Bytewords.encode(BytewordsStyle.STANDARD, bytes1));
        assertEquals(uriEncoded1, Bytewords.encode(BytewordsStyle.URI, bytes1));
        assertEquals(minimalEncoded1, Bytewords.encode(BytewordsStyle.MINIMAL, bytes1));

        assertArrayEquals(bytes1, Bytewords.decode(BytewordsStyle.STANDARD, standardEncoded1));
        assertArrayEquals(bytes1, Bytewords.decode(BytewordsStyle.URI, uriEncoded1));
        assertArrayEquals(bytes1, Bytewords.decode(BytewordsStyle.MINIMAL, minimalEncoded1));

        byte[] bytes2 = hex2Bytes(
                "F5D714C6F1EB453BD1CDA512969E7487E5D4139F1125EFF0FD0B6DBF25F22678D" +
                "F299CBDF2FE93CC42A3D8AFBF48A936203C90E6D289B8C52171580E9D1FB12E01" +
                "73CD45E19641EB3A9041F0854571F73F35F2A5A0901A0D4FED85475245FEA58A2" +
                "95518");
        String standardEncoded2 = "yank toys bulb skew when warm free fair tent swan open brag " +
                                  "mint noon jury list view tiny brew note body data webs what " +
                                  "zinc bald join runs data whiz days keys user diet news ruby " +
                                  "whiz zone menu surf flew omit trip pose runs fund part even " +
                                  "crux fern math visa tied loud redo silk curl jugs hard beta " +
                                  "next cost puma drum acid junk swan free very mint flap warm " +
                                  "fact math flap what limp free jugs yell fish epic whiz open " +
                                  "numb math city belt glow wave limp fuel grim free zone open " +
                                  "love diet gyro cats fizz holy city puff";
        String minimalEncoded2 = "yktsbbswwnwmfefrttsnonbgmtnnjyltvwtybwnebydawswtzcbdjnrsdawzd" +
                                 "sksurdtnsrywzzemusffwottppersfdptencxfnmhvatdldroskcljshdbant" +
                                 "ctpadmadjksnfevymtfpwmftmhfpwtlpfejsylfhecwzonnbmhcybtgwwelpf" +
                                 "lgmfezeonledtgocsfzhycypf";

        assertEquals(standardEncoded2, Bytewords.encode(BytewordsStyle.STANDARD, bytes2));
        assertEquals(minimalEncoded2, Bytewords.encode(BytewordsStyle.MINIMAL, bytes2));
        assertArrayEquals(bytes2, Bytewords.decode(BytewordsStyle.STANDARD, standardEncoded2));
        assertArrayEquals(bytes2, Bytewords.decode(BytewordsStyle.MINIMAL, minimalEncoded2));
    }

    @Test
    public void testWord() {
        assertEquals("able", Bytewords.word(0));
        assertEquals("aunt", Bytewords.word(7));
        assertEquals("duty", Bytewords.word(48));
        assertEquals("easy", Bytewords.word(50));
        assertEquals("zone", Bytewords.word(254));
        assertEquals("zoom", Bytewords.word(255));

        assertThrows("Bytewords.word(-1)", BytewordsException.class, () -> Bytewords.word(-1));
        assertThrows("Bytewords.word(256)", BytewordsException.class, () -> Bytewords.word(256));
    }

    @Test
    public void testError() {
        assertThrows("Bytewords.encode(null, new byte[]{})",
                     BytewordsException.class,
                     () -> Bytewords.encode(null, new byte[]{}));
        assertThrows("Bytewords.decode(null, \"\")",
                     BytewordsException.class,
                     () -> Bytewords.decode(null, ""));
        assertThrows("Bytewords.encode(BytewordsStyle.STANDARD, null)",
                     BytewordsException.class,
                     () -> Bytewords.encode(BytewordsStyle.STANDARD, null));
        assertThrows("Bytewords.decode(BytewordsStyle.URI, \"\")",
                     BytewordsException.class,
                     () -> Bytewords.decode(BytewordsStyle.URI, ""));
        assertThrows("Bytewords.decode(BytewordsStyle.STANDARD, \"aeadaolazmjendeoti\"",
                     BytewordsException.class,
                     () -> Bytewords.decode(BytewordsStyle.STANDARD, "aeadaolazmjendeoti"));
    }
}
