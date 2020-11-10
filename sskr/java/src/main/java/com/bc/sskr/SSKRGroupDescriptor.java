package com.bc.sskr;

public class SSKRGroupDescriptor {

    private final int threshold;

    private final int count;

    public SSKRGroupDescriptor(int threshold, int count) {
        this.threshold = threshold;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public int getThreshold() {
        return threshold;
    }
}
