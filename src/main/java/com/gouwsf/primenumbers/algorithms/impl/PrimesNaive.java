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
    public void extendSegment(int fromExclusive, int toInclusive, ArrayList<Integer> basePrimes) {
        if (toInclusive <= Math.max(1, fromExclusive)) return;

        var capacity = Math.max(determineInitialCapacity(toInclusive), basePrimes.size());
        basePrimes.ensureCapacity(capacity);

        if ((fromExclusive + 1) % 2 == 0) { // if even
            fromExclusive++;
        }

        int lastKnown = basePrimes.get(basePrimes.size() - 1);
        for (int i = fromExclusive + 1; i <= toInclusive; i += 2) {
            if (isPrimeByPrimes(i, basePrimes) && i > lastKnown) {
                basePrimes.add(i);
            }
        }
    }

    private static boolean isPrimeByPrimes(int x, List<Integer> existingPrimes) {
        for (int p : existingPrimes) {
            if ((long) p * p > x) break;
            if (x % p == 0) return false;
        }
        return true;
    }
}
