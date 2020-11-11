package com.bc.shamir;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import static com.bc.shamir.TestUtils.bytes2Hex;
import static com.bc.shamir.TestUtils.hex2Bytes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(JUnit4.class)
public class ShamirTest {

    @Test
    public void testShamirValidFlow() {
        // test Shamir#splitSecret
        byte[] secret = hex2Bytes("00112233445566778899aabbccddeeff");
        ShamirShare[] shares = Shamir.splitSecret((short) 2, (short) 3, secret, len -> {
            byte b = 0;
            final byte[] ret = new byte[len];
            for (int i = 0; i < len; i++) {
                ret[i] = b;
                b += 17;
            }
            return ret;
        });

        String[] expectedShares = new String[]{"3dc2ff5b2a3b08193a2b1809a2b38091",
                "c7fe6b576e7f4c5df6e7d4c5e6f7c4d5",
                "d2bacc43a2b38091b9a89b8a2a3b0819"};
        for (int i = 0; i < shares.length; i++) {
            assertEquals((short) i, shares[i].getIndex());
            assertEquals(expectedShares[i], bytes2Hex(shares[i].getData()));
        }

        // test Shamir#recoverSecret
        ShamirShare[] recoveredShares = IntStream.range(0, shares.length)
                                                 .filter(i -> i != 1)
                                                 .mapToObj(i -> shares[i])
                                                 .toArray(ShamirShare[]::new);
        final byte[] recoveredSecret = Shamir.recoverSecret(recoveredShares);
        assertEquals(bytes2Hex(secret), bytes2Hex(recoveredSecret));

        ShamirShare[] badRecoveredShares = IntStream.range(0, recoveredShares.length)
                                                    .filter(i -> i != 0)
                                                    .mapToObj(i -> recoveredShares[i])
                                                    .toArray(ShamirShare[]::new);
        final byte[] badRecoveredSecret = Shamir.recoverSecret(badRecoveredShares);
        assertNotEquals(bytes2Hex(secret), bytes2Hex(badRecoveredSecret));

    }

    @Test
    public void testShamirError() throws Exception {
        RandomFunc randomFunc = len -> new byte[]{0x00, 0x01};
        byte[] secret = hex2Bytes("00112233445566778899aabbccddeeff");

        internalTest(() -> Shamir.splitSecret((short) -1, (short) 5, secret, randomFunc));
        internalTest(() -> Shamir.splitSecret((short) 3, (short) -1, secret, randomFunc));
        internalTest(() -> Shamir.splitSecret((short) 256, (short) 4, secret, randomFunc));
        internalTest(() -> Shamir.splitSecret((short) 2, (short) 32267, null, randomFunc));
        internalTest(() -> Shamir.splitSecret((short) 2, (short) 3, null, randomFunc));
        internalTest(() -> Shamir.splitSecret((short) 2, (short) 3, secret, null));

        internalTest(() -> Shamir.recoverSecret(null));
        internalTest(() -> Shamir.recoverSecret(new ShamirShare[]{}));
        internalTest(() -> Shamir.recoverSecret(new ShamirShare[]{
                new ShamirShare((short) 0, hex2Bytes("3dc2ff5b2a3b08193a2b1809a2b38091")),
                new ShamirShare((short) 1, hex2Bytes("c7fe6b576e7f4c5df6e7d4c5e6f")),
                new ShamirShare((short) 2, hex2Bytes("d2ba"))}));
    }

    private void internalTest(Callable callable) throws Exception {
        try {
            callable.call();
            throw new RuntimeException("test is failed");
        } catch (ShamirException ignore) {
        }
    }
}
