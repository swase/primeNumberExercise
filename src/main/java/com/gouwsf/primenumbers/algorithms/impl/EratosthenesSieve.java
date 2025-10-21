package com.gouwsf.primenumbers.algorithms.impl;

import com.gouwsf.primenumbers.algorithms.PrimesGenerator;
import com.gouwsf.primenumbers.model.AlgorithmType;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Getter
public class EratosthenesSieve implements PrimesGenerator {

    private final AlgorithmType type = AlgorithmType.ERATOS;

    /**
     * Computes all prime numbers up to a given upper bound using the Sieve of Eratosthenes algorithm
     *
     * @param limit the upper bound (inclusive) for prime generation;
     * @return a list of all prime numbers ≤ {@code limit}, in ascending order;
     */
    public List<Integer> determinePrimes(int limit) {
        boolean[] sieve = new boolean[limit + 1];
        Arrays.fill(sieve, true);

        for (int p = 2; p * p <= limit; p++) {
            if(sieve[p]) {
                for (int j = p * p; j <= limit; j += p) {
                    sieve[j] = false;
                }
            }
        }
        return convertSieveToList(sieve);
    }

    @Override
    public List<Integer> determinePrimes(int fromExclusive, int toInclusive, List<Integer> basePrimes) {
        if (fromExclusive == 0) return determinePrimes(toInclusive);

        if (basePrimes == null) {
            throw new IllegalArgumentException("basePrimes must not be null");
        }
        if (toInclusive <= fromExclusive || toInclusive < 2) List.of();

        final int start = Math.max(fromExclusive + 1, 2);
        final int end = toInclusive;
        final int len = end - start + 1;
        if (len <= 0) List.of();

        final boolean[] sieve = new boolean[len];

        for (int p : basePrimes) {
            if (p * p > end) break;

            // First multiple of p within [start, end]
            long first = Math.max((long) p * p, ((start + p - 1L) / p) * p);
            for (long j = first; j <= end; j += p) {
                sieve[(int)(j - start)] = true;
            }
        }
        // The extension from base primes
        var res = new ArrayList<Integer>(determineInitialCapacity(len));

        for (int i = 0; i < len; i++) {
            if (!sieve[i]) {
                res.add(start + i);
            }
        }
        return res;
    }



}
