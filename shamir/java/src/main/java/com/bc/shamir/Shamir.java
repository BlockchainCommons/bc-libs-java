package com.bc.shamir;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Shamir {

    public static ShamirShard[] splitSecret(short threshold,
                                            short shardsCount,
                                            byte[] secret,
                                            RandomFunc randomFunc) {
        if (secret == null) {
            throw new ShamirException("secret is NULL");
        }

        if (randomFunc == null) {
            throw new ShamirException("randomFunc is NULL");
        }

        if (threshold < 0 || shardsCount < 0) {
            throw new ShamirException("threshold or shardsCount is negative number");
        }

        final byte[] output = new byte[secret.length * shardsCount];

        int ret = ShamirJni.split_secret(threshold, shardsCount, secret, output, randomFunc);
        if (ret <= 0) {
            throw new ShamirException("Shamir splitSecret error");
        }

        ShamirShard[] shards = new ShamirShard[shardsCount];
        for (int i = 0; i < shardsCount; i++) {
            int offset = i * secret.length;
            byte[] data = Arrays.copyOfRange(output, offset, offset + secret.length);
            shards[i] = new ShamirShard((short) i, data);
        }

        return shards;
    }

    public static byte[] recoverSecret(ShamirShard[] shards) {
        if (shards == null) {
            throw new ShamirException("shards is NULL");
        }

        int shardsCount = shards.length;
        if (shardsCount == 0) {
            throw new ShamirException("No shards is provided");
        }

        Set<Integer> shardLengths = new HashSet<>();
        for (ShamirShard shard : shards) {
            shardLengths.add(shard.getData().length);
        }
        if (shardLengths.size() != 1) {
            throw new ShamirException("Shards don't all have the same length");
        }

        int shardLength = shardLengths.iterator().next();
        byte[] indexes = new byte[shardsCount];
        byte[][] shardsBytes = new byte[shardsCount][];

        for (int i = 0; i < shardsCount; i++) {
            indexes[i] = (byte) shards[i].getIndex();
            shardsBytes[i] = shards[i].getData();
        }

        final byte[] secret = new byte[shardLength];
        int ret = ShamirJni.recover_secret((short) shardsCount,
                indexes,
                shardsBytes,
                shardLength,
                secret);
        if (ret != shardLength) {
            throw new ShamirException("Shamir recoverSecret error");
        }

        return secret;
    }
}
