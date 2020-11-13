package com.bc.sskr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import static com.bc.sskr.TestUtils.bytes2Hex;
import static com.bc.sskr.TestUtils.hex2Bytes;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class SSKRTest {

    @Test
    public void testSSKRValidFlow() {
        // check SSKR#countShares
        int groupThreshold = 2;
        SSKRGroupDescriptor[] groups = new SSKRGroupDescriptor[]{new SSKRGroupDescriptor(2, 3),
                new SSKRGroupDescriptor(3, 5)};
        int expectedShareCount = 8;
        int shareCount = SSKR.countShares(groupThreshold, groups);
        assertEquals(expectedShareCount, shareCount);

        // check SSKR#generate
        byte[] secret = hex2Bytes("00112233445566778899aabbccddeeff");
        SSKRShare[][] shareGroup = SSKR.generate(groupThreshold, groups, secret, len -> {
            byte b = 0;
            final byte[] ret = new byte[len];
            for (int i = 0; i < len; i++) {
                ret[i] = b;
                b += 17;
            }
            return ret;
        });

        String[][] expectedShareData = new String[][]{new String[]{
                "1100110100bae4b1dda4b58697b3a291802c3d0e1f",
                "11001101016c0158178e9facbdcddceffe06172435",
                "11001101020d357852f0e1d2c34f5e6d7c78695a4b",},
                new String[]{"110011120000112233445566778899aabbccddeeff",
                        "1100111201f1a1a2262a3b08193a2b1809a2b38091",
                        "1100111202f0a55798b3a291808a9ba8b93b2a1908",
                        "11001112030115d78dddccffee38291a0b55447766",
                        "1100111204df736b4c1d0c3f2e637241509584b7a6"}};

        for (int i = 0; i < shareGroup.length; i++) {
            SSKRShare[] shares = shareGroup[i];
            for (int j = 0; j < shares.length; j++) {
                assertEquals(expectedShareData[i][j], bytes2Hex(shareGroup[i][j].getData()));
            }
        }

        // check SSKR#combine
        SSKRShare[] flattenShares = Arrays.stream(shareGroup)
                                          .flatMap(Arrays::stream)
                                          .toArray(SSKRShare[]::new);
        SSKRShare[] recoveredShares = IntStream.range(0, flattenShares.length)
                                               .filter(i -> i != 1 && i != 3 && i != 6)
                                               .mapToObj(i -> flattenShares[i])
                                               .toArray(SSKRShare[]::new);

        byte[] recoveredSecret = SSKR.combine(recoveredShares);
        assertEquals(bytes2Hex(secret), bytes2Hex(recoveredSecret));

        SSKRShare[] invalidRecoveredShares = IntStream.range(0, recoveredShares.length)
                                                      .filter(i -> i != 3)
                                                      .mapToObj(i -> recoveredShares[i])
                                                      .toArray(SSKRShare[]::new);
        try {
            SSKR.combine(invalidRecoveredShares);
            throw new RuntimeException("check SSKR#combine failed");
        } catch (SSKRException ignore) {
        }
    }

    @Test
    public void testSSKRError() throws Exception {

        final SSKRGroupDescriptor[] groups = new SSKRGroupDescriptor[]{new SSKRGroupDescriptor(2,
                                                                                               3)};
        final byte[] secret = hex2Bytes("00112233445566778899aabbccddeeff");
        final RandomFunc randomFunc = (len) -> new byte[]{0x01, 0x02};

        // check SSKR#countShares
        internalTest(() -> SSKR.countShares(2, null));
        internalTest(() -> SSKR.countShares(-1, groups));

        // check SSKR#generate
        internalTest(() -> SSKR.generate(-1, groups, secret, randomFunc));
        internalTest(() -> SSKR.generate(2, null, secret, randomFunc));
        internalTest(() -> SSKR.generate(2, groups, new byte[]{0x7F}, randomFunc));
        internalTest(() -> SSKR.generate(2, groups, null, randomFunc));
        internalTest(() -> SSKR.generate(2, groups, secret, null));

        // check SSKR#combine
        internalTest(() -> SSKR.combine(null));
        internalTest(() -> SSKR.combine(new SSKRShare[]{new SSKRShare(new byte[]{0x00, 0x01, 0x02}),
                new SSKRShare(new byte[]{})}));

    }

    private void internalTest(Callable callable) throws Exception {
        try {
            callable.call();
            throw new RuntimeException("test is failed");
        } catch (SSKRException ignore) {
        }
    }
}
