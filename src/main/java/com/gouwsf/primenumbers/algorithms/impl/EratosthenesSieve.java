package com.gouwsf.primenumbers.algorithms.impl;

import com.gouwsf.primenumbers.algorithms.PrimesGenerator;
import com.gouwsf.primenumbers.model.AlgorithmType;
import lombok.Getter;
import org.springframework.stereotype.Component;

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
}
