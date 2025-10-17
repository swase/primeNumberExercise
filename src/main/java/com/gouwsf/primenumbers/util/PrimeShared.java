package com.gouwsf.primenumbers.util;

public class PrimeShared {
    public static int determineInitialCapacity(int n) {
        if (n <= 1) return 0;
        return (n > 10) ? (int) (n / Math.log(n)) : n;
    }
}
