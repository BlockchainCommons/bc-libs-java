package com.bc.sskr;

public class SSKRShard {

    private final byte[] data;

    public SSKRShard(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
