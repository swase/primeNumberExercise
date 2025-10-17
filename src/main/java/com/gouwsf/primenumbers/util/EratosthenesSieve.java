package com.gouwsf.primenumbers.util;

import java.util.ArrayList;
import java.util.List;

public class EratosthenesSieve {
    public static List<Integer> determinePrimes(int n) {
        boolean[] sieve = new boolean[n + 1];
        for (int i = 0; i <=n; i++) {
            sieve[i] = true;
        }

        for (int p = 2; p * p <= n; p++) {
            if(sieve[p]) {
                for (int j = p * p; j <= n; j += p) {
                    sieve[j] = false;
                }
            }
        }

        var result = new ArrayList<Integer>(PrimeShared.determineInitialCapacity(n));
        for (int k = 2; k <= n; k++) {
            if (sieve[k]) {
                result.add(k);
            }
        }
        return result;
    }

}
