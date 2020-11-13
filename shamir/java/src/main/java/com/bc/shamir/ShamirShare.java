package com.bc.shamir;

public class ShamirShare {

    private final byte[] data;

    private final short index;

    public ShamirShare(short index, byte[] data) {
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
