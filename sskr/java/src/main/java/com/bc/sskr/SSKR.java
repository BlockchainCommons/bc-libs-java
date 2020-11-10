package com.bc.sskr;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SSKR {

    public static int countShares(int groupThreshold, SSKRGroupDescriptor[] groups) {
        int ret = SSKRJni.SSKR_count_shares(groupThreshold, groups);
        if (ret >= 0)
            return ret;
        throw new SSKRException("SSKR countShares error: " + ret);
    }

    public static SSKRShare[][] generate(int groupThreshold,
                                         SSKRGroupDescriptor[] groups,
                                         byte[] secret,
                                         RandomFunc randomFunc) {
        if (groups == null || secret == null || randomFunc == null) {
            throw new SSKRException("NULL params is not accepted");
        }

        int shareLength = secret.length + 5;
        int shareCount = countShares(groupThreshold, groups);
        int outputLength = shareCount * shareLength;

        int[] resultShareLength = new int[1];
        byte[] output = new byte[outputLength];

        int ret = SSKRJni.SSKR_generate(groupThreshold,
                                        groups,
                                        secret,
                                        resultShareLength,
                                        output,
                                        randomFunc);

        if (ret <= 0) {
            throw new SSKRException("SSKR generate error");
        }

        SSKRShare[][] groupShares = new SSKRShare[groups.length][];
        int offset = 0;
        for (int i = 0; i < groups.length; i++) {
            SSKRGroupDescriptor group = groups[i];
            SSKRShare[] shares = new SSKRShare[group.getCount()];
            for (int j = 0; j < group.getCount(); j++) {
                byte[] data = Arrays.copyOfRange(output, offset, offset + shareLength);
                shares[j] = new SSKRShare(data);
                offset += shareLength;
            }
            groupShares[i] = shares;
        }

        return groupShares;
    }

    public static byte[] combine(SSKRShare[] shares) {
        if (shares == null) {
            throw new SSKRException("NULL params is not accepted");
        }

        int sharesCount = shares.length;
        if (sharesCount == 0) {
            throw new SSKRException("No shares is provided");
        }

        Set<Integer> shareLengths = new HashSet<>();
        for (SSKRShare share : shares) {
            shareLengths.add(share.getData().length);
        }
        if (shareLengths.size() != 1) {
            throw new SSKRException("Shares don't all have the same length");
        }

        int shareLength = shareLengths.iterator().next();
        int secretLength = shareLength - 5;
        byte[][] sharesBytes = new byte[sharesCount][];
        for (int i = 0; i < sharesCount; i++) {
            sharesBytes[i] = shares[i].getData();
        }

        byte[] secret = new byte[secretLength];
        int ret = SSKRJni.SSKR_combine(sharesBytes, shareLength, sharesCount, secret);
        if (ret <= 0) {
            throw new SSKRException("SSKR combine error");
        }

        return secret;
    }


}
