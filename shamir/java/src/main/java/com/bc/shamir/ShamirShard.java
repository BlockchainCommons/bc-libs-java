package com.bc.shamir;

public class ShamirShard {

    private final byte[] data;

    private final short index;

    public ShamirShard(short index, byte[] data) {
        this.index = index;
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public short getIndex() {
        return index;
    }
}
