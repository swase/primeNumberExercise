package com.gouwsf.primenumbers.algorithms.impl;

import com.gouwsf.primenumbers.algorithms.PrimesGenerator;
import com.gouwsf.primenumbers.model.AlgorithmType;
import lombok.Getter;
import org.springframework.stereotype.Component;

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

        boolean[] sieve = doInitialPrimesEstimate(limit);
        filterOutNonPrimes(sieve, limit);
        return convertSieveToList(sieve);
    }

    private boolean[] doInitialPrimesEstimate(int limit) {
        boolean[] sieve = new boolean[limit + 1];

        // Set first primes up to 3
        sieve[0] = false;
        sieve[1] = false;
        sieve[2] = true;
        sieve[3] = true;

        for (int x = 1; x * x <= limit; x++) {
            for (int y = 1; y * y <= limit; y++) {
                int n1 = (4 * x * x) + (y * y); // 4x² + y²
                int n2 = (3 * x * x) + (y * y); // 3x² + y²
                int n3 = (3 * x * x) - (y * y); // 3x²-y²

                if (n1 <= limit && (n1 % 12 == 1 || n1 % 12 == 5)) {
                    sieve[n1] = !sieve[n1];
                }

                if (n2 <= limit && (n2 % 12 ==7)) {
                    sieve[n2] = !sieve[n2];
                }

                if (x > y &&
                        n3 <= limit && (n3 % 12 == 11)) {
                    sieve[n3] ^= !sieve[n3];
                }
            }
        }
        return sieve;
    }

    private void filterOutNonPrimes(boolean[] sieve, int limit) {
        for (int r= 5; r * r < limit; r++) {
            if (sieve[r]) {
                for (int i = r * r; i < limit; i += r * r) {
                    sieve[i] = false;
                }
            }
        }
    }
}
