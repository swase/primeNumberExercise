package com.gouwsf.primenumbers.algorithms;

import com.gouwsf.primenumbers.model.AlgorithmType;

import java.util.ArrayList;
import java.util.List;

public interface PrimesGenerator {
    AlgorithmType getType();
    List<Integer> determinePrimes(int limit);

    default int determineInitialCapacity(int n) {
        if (n <= 1) return 0;
        return (n > 10) ? (int) (n / Math.log(n)) : n;
    }

    default List<Integer> convertSieveToList(boolean[] sieve) {
        var limit = sieve.length > 1 ? sieve.length - 1 : 0;

        var primesList = new ArrayList<Integer>(determineInitialCapacity(sieve.length));
        for (int k = 2; k <= limit; k++) {
            if (sieve[k]) {
                primesList.add(k);
            }
        }
        return primesList;
    }
}
