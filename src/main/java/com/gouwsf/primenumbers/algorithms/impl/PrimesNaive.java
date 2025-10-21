package com.gouwsf.primenumbers.algorithms.impl;

import com.gouwsf.primenumbers.algorithms.PrimesGenerator;
import com.gouwsf.primenumbers.model.AlgorithmType;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Getter
public class PrimesNaive implements PrimesGenerator {

    private final AlgorithmType type = AlgorithmType.NAIVE;

    /**
     * Computes all prime numbers up to a given upper bound using optimised naive approach.
     *
     * @param limit the upper bound (inclusive) for prime generation;
     * @return a list of all prime numbers ≤ {@code limit}, in ascending order;
     */
    public List<Integer> determinePrimes(int limit) {
        if (limit < 2) return Collections.emptyList();
        if (limit == 2) return List.of(2);

        List<Integer> primes = new ArrayList<>(determineInitialCapacity(limit));
        primes.add(2);

        for (int j = 3; j <= limit; j += 2) {
            if (isPrimeByPrimes(j, primes)) {
                primes.add(j);
            }
        }
        return primes;
    }

    @Override
    public List<Integer> determinePrimes(int fromExclusive, int toInclusive, List<Integer> basePrimes) {
        if (toInclusive < 2 || toInclusive <= fromExclusive) return List.of();

        if (fromExclusive < 2) {
            return determinePrimes(toInclusive);
        }

        var res = new ArrayList<Integer>(determineInitialCapacity(toInclusive - fromExclusive));
        int start = fromExclusive + 1;
        if (start % 2 == 0) {
            if (start == 2) {
                res.add(2);
            }
            start++;
        }

        for (int i = start; i <= toInclusive; i += 2) {
            if (isPrimeByPrimes(i, basePrimes)) {
                res.add(i);
            }
        }
        return res;
    }

    private static boolean isPrimeByPrimes(int x, List<Integer> existingPrimes) {
        for (int p : existingPrimes) {
            if ((long) p * p > x) break;
            if (x % p == 0) return false;
        }
        return true;
    }
}
