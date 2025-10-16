package com.gouwsf.primenumbers.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrimesNaive {

    public static List<Integer> getPrimes(int limit) {
        if (limit < 2) return Collections.emptyList();
        if (limit == 2) return List.of(2);

        List<Integer> res = new ArrayList<>();
        res.add(2);

        for (int j = 3; j <= limit; j += 2) {
            if (isPrime(j)) {
                res.add(j);
            }
        }
        return res;
    }

    // Ignore even
    private static boolean isPrime(int n) {
        for (int i = 3; i <= n; i += 2) {
            if (n % i == 0 && n != i) {
                return false;
            }
        }
        return true;
    }

    private static boolean isEven(long n) {
        return n % 2 == 0;
    }
}
