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
public class AtkinsSieve implements PrimesGenerator {
    private final AlgorithmType type = AlgorithmType.ATKINS;

    /**
     * Computes all prime numbers up to a given upper bound using the Sieve of Atkins algorithm
     *
     * @param limit the upper bound (inclusive) for prime generation;
     * @return a list of all prime numbers ≤ {@code limit}, in ascending order;
     */
    public List<Integer> determinePrimes(int limit) {
        if (limit < 2) return Collections.emptyList();
        if (limit == 2) return List.of(2);

        boolean[] sieve = doInitialPrimesEstimate(0, limit);
        filterOutNonPrimes(sieve, limit);
        return convertSieveToList(sieve);
    }

    @Override
    public void extendSegment(int fromExclusive, int toInclusive, ArrayList<Integer> basePrimes) {
        if (basePrimes == null) throw new IllegalArgumentException("basePrimes must not be null");
        if (toInclusive <= fromExclusive || toInclusive < 2) return;

        final int lower = Math.max(fromExclusive + 1, 2); // (fromExclusive, ...]
        final int upper = toInclusive;
        if (upper < lower) return;

        // 1) Atkin quadratic toggling
        boolean[] segment = doInitialPrimesEstimate(lower, upper);

        // 2) Eliminate multiples of prime squares
        filterOutNonPrimes(segment, lower, upper, basePrimes);

        // 3) Append discovered primes to existing
        final int lastKnown = basePrimes.isEmpty() ? 1 : basePrimes.get(basePrimes.size() - 1);
        for (int i = 0; i < segment.length; i++) {
            if (segment[i]) {
                int candidate = lower + i;
                if (candidate > lastKnown) {
                    basePrimes.add(candidate);
                }
            }
        }
    }


    /**
     * Computes initial primes using Atkins 3 quadratics and mod 12 conditions
     *
     * @param lower the lower bound
     * @param upper the lower bound
     * @return an array of bool values of intial estimates
     */
    private boolean[] doInitialPrimesEstimate(int lower, int upper) {
        if (upper < 2 || upper < lower) {
            return new boolean[0];
        }

        final int start = Math.max(lower, 0);
        final int end = upper;
        final int len = end - start + 1;
        boolean[] sieve = new boolean[len];

        // handle 2 and 3 explicitly
        if (2 >= start && 2 <= end) sieve[2 - start] = true;
        if (3 >= start && 3 <= end) sieve[3 - start] = true;

        int xLimit = (int) Math.sqrt(end);
        for (int x = 1; x <= xLimit; x++) {
            int x2 = x * x;
            for (int y = 1; y <= xLimit; y++) {
                int y2 = y * y;

                int n1 = 4 * x2 + y2;
                if (n1 >= start && n1 <= end && (n1 % 12 == 1 || n1 % 12 == 5)) {
                    sieve[n1 - start] = !sieve[n1 - start];
                }

                int n2 = 3 * x2 + y2;
                if (n2 >= start && n2 <= end && (n2 % 12 == 7)) {
                    sieve[n2 - start] = !sieve[n2 - start];
                }

                int n3 = 3 * x2 - y2;
                if (x > y && n3 >= start && n3 <= end && (n3 % 12 == 11)) {
                    sieve[n3 - start] = !sieve[n3 - start];
                }
            }
        }
        return sieve;
    }

    /**
     * Elimination of multiples of prime squares
     *
     * @param sieve initial determined primes
     * @param limit inclusive last prime to check for
     */
    private void filterOutNonPrimes(boolean[] sieve, int limit) {
        for (int r = 5; (long) r * r <= limit; r++) {
            if (sieve[r]) {
                long r2 = (long) r * r;
                for (long i = r2; i <= limit; i += r2) {
                    sieve[(int) i] = false;
                }
            }
        }
    }

    /**
     * Segmented elimination of multiples of prime squares within [lower, upper].
     * The 'segment' array has length (upper - lower + 1) and segment[i] corresponds to n = lower + i.
     * `basePrimes` must contain at least all primes up to floor(sqrt(upper)).
     */
    private void filterOutNonPrimes(boolean[] segment, int lower, int upper, List<Integer> basePrimes) {
        if (segment.length == 0 || upper < 2) return;

        int sqrtUpper = (int) Math.floor(Math.sqrt(upper));
        for (int r : basePrimes) {
            if (r < 5) continue;
            if (r > sqrtUpper) break;

            long r2 = (long) r * r;

            long first = Math.max(r2, ((lower + r2 - 1) / r2) * r2);

            for (long n = first; n <= upper; n += r2) {
                int idx = (int) (n - lower);
                if (idx >= 0 && idx < segment.length) {
                    segment[idx] = false;
                }
            }
        }
    }
}
