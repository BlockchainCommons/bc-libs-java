package com.bc.shamir;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Shamir {

    public static ShamirShare[] splitSecret(short threshold,
                                            short shareCount,
                                            byte[] secret,
                                            RandomFunc randomFunc) {
        if (secret == null) {
            throw new ShamirException("secret is NULL");
        }

        if (randomFunc == null) {
            throw new ShamirException("randomFunc is NULL");
        }

        if (threshold < 0 || shareCount < 0) {
            throw new ShamirException("threshold or shareCount is negative number");
        }

        final byte[] output = new byte[secret.length * shareCount];

        int ret = ShamirJni.split_secret(threshold, shareCount, secret, output, randomFunc);
        if (ret <= 0) {
            throw new ShamirException("Shamir splitSecret error");
        }

        ShamirShare[] shares = new ShamirShare[shareCount];
        for (int i = 0; i < shareCount; i++) {
            int offset = i * secret.length;
            byte[] data = Arrays.copyOfRange(output, offset, offset + secret.length);
            shares[i] = new ShamirShare((short) i, data);
        }

        return shares;
    }

    public static byte[] recoverSecret(ShamirShare[] shares) {
        if (shares == null) {
            throw new ShamirException("shares is NULL");
        }

        int sharesCount = shares.length;
        if (sharesCount == 0) {
            throw new ShamirException("No share is provided");
        }

        Set<Integer> shareLengths = new HashSet<>();
        for (ShamirShare share : shares) {
            shareLengths.add(share.getData().length);
        }
        if (shareLengths.size() != 1) {
            throw new ShamirException("Shares don't all have the same length");
        }

        int shareLength = shareLengths.iterator().next();
        byte[] indexes = new byte[sharesCount];
        byte[][] sharesBytes = new byte[sharesCount][];

        for (int i = 0; i < sharesCount; i++) {
            indexes[i] = (byte) shares[i].getIndex();
            sharesBytes[i] = shares[i].getData();
        }

        final byte[] secret = new byte[shareLength];
        int ret = ShamirJni.recover_secret((short) sharesCount,
                                           indexes,
                                           sharesBytes,
                                           shareLength,
                                           secret);
        if (ret != shareLength) {
            throw new ShamirException("Shamir recoverSecret error");
        }

        return secret;
    }
}
