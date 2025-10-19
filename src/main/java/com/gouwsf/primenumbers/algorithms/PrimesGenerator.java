package com.gouwsf.primenumbers.algorithms;

import com.gouwsf.primenumbers.model.AlgorithmType;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a contract for prime number generation algorithms.
 * <p>
 * Implementations provide a concrete strategy for finding all
 * prime numbers up to a given limit (e.g., Sieve of Eratosthenes,
 * Sieve of Atkin, naive iteration).
 */
public interface PrimesGenerator {

    /**
     * Returns the type of prime generation algorithm represented by this implementation.
     *
     * @return the {@link AlgorithmType} used by this generator
     */
    AlgorithmType getType();

    /**
     * Computes all prime numbers less than or equal to the specified limit.
     *
     * @param limit the upper bound (inclusive) up to which primes are generated
     * @return a list of prime numbers ≤ {@code limit}, in ascending order
     */
    List<Integer> determinePrimes(int limit);

    /**
     * Estimates an initial capacity for the list of prime numbers
     * up to {@code n}, using the prime number theorem approximation {@code n / log(n)}.
     * <p>
     * This helps reduce re-allocations when building prime lists.
     *
     * @param n the upper bound for prime generation
     * @return the estimated number of primes ≤ {@code n}, or 0 if {@code n ≤ 1}
     */
    default int determineInitialCapacity(int n) {
        if (n <= 1) return 0;
        return (n > 10) ? (int) (n / Math.log(n)) : n;
    }

    /**
     * Converts a boolean sieve array into a list of prime numbers.
     *
     * @param sieve a boolean array where {@code true} indicates a prime candidate
     * @return a list of all indices marked prime in ascending order
     */
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
