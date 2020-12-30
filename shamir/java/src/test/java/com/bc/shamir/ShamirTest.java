package com.bc.shamir;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.stream.IntStream;

import static com.bc.shamir.util.TestUtils.assertThrows;
import static com.bc.shamir.util.TestUtils.bytes2Hex;
import static com.bc.shamir.util.TestUtils.hex2Bytes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(JUnit4.class)
public class ShamirTest {

    @Test
    public void testShamirValidFlow() {
        // test Shamir#splitSecret
        byte[] secret = hex2Bytes("00112233445566778899aabbccddeeff");
        ShamirShard[] shards = Shamir.splitSecret((short) 2, (short) 3, secret, len -> {
            byte b = 0;
            final byte[] ret = new byte[len];
            for (int i = 0; i < len; i++) {
                ret[i] = b;
                b += 17;
            }
            return ret;
        });

        String[] expectedShards = new String[]{"3dc2ff5b2a3b08193a2b1809a2b38091",
                "c7fe6b576e7f4c5df6e7d4c5e6f7c4d5",
                "d2bacc43a2b38091b9a89b8a2a3b0819"};
        for (int i = 0; i < shards.length; i++) {
            assertEquals((short) i, shards[i].getIndex());
            assertEquals(expectedShards[i], bytes2Hex(shards[i].getData()));
        }

        // test Shamir#recoverSecret
        ShamirShard[] recoveredShards = IntStream.range(0, shards.length)
                .filter(i -> i != 1)
                .mapToObj(i -> shards[i])
                .toArray(ShamirShard[]::new);
        final byte[] recoveredSecret = Shamir.recoverSecret(recoveredShards);
        assertEquals(bytes2Hex(secret), bytes2Hex(recoveredSecret));

        ShamirShard[] badRecoveredShards = IntStream.range(0, recoveredShards.length)
                .filter(i -> i != 0)
                .mapToObj(i -> recoveredShards[i])
                .toArray(ShamirShard[]::new);
        final byte[] badRecoveredSecret = Shamir.recoverSecret(badRecoveredShards);
        assertNotEquals(bytes2Hex(secret), bytes2Hex(badRecoveredSecret));

    }

    @Test
    public void testShamirError() throws Exception {
        RandomFunc randomFunc = len -> new byte[]{0x00, 0x01};
        byte[] secret = hex2Bytes("00112233445566778899aabbccddeeff");

        assertThrows(ShamirException.class, () -> Shamir.splitSecret((short) -1, (short) 5, secret, randomFunc));
        assertThrows(ShamirException.class, () -> Shamir.splitSecret((short) 3, (short) -1, secret, randomFunc));
        assertThrows(ShamirException.class, () -> Shamir.splitSecret((short) 256, (short) 4, secret, randomFunc));
        assertThrows(ShamirException.class, () -> Shamir.splitSecret((short) 2, (short) 32267, null, randomFunc));
        assertThrows(ShamirException.class, () -> Shamir.splitSecret((short) 2, (short) 3, null, randomFunc));
        assertThrows(ShamirException.class, () -> Shamir.splitSecret((short) 2, (short) 3, secret, null));

        assertThrows(ShamirException.class, () -> Shamir.recoverSecret(null));
        assertThrows(ShamirException.class, () -> Shamir.recoverSecret(new ShamirShard[]{}));
        assertThrows(ShamirException.class, () -> Shamir.recoverSecret(new ShamirShard[]{
                new ShamirShard((short) 0, hex2Bytes("3dc2ff5b2a3b08193a2b1809a2b38091")),
                new ShamirShard((short) 1, hex2Bytes("c7fe6b576e7f4c5df6e7d4c5e6f")),
                new ShamirShard((short) 2, hex2Bytes("d2ba"))}));
    }
}
