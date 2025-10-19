package com.gouwsf.primenumbers.algorithms;

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

    public List<Integer> determinePrimes(int n) {
        if (n < 2) return Collections.emptyList();
        if (n == 2) return List.of(2);

        List<Integer> primes = new ArrayList<>(determineInitialCapacity(n));
        primes.add(2);

        for (int j = 3; j <= n; j += 2) {
            if (isPrimeByPrimes(j, primes)) {
                primes.add(j);
            }
        }
        return primes;
    }

    private static boolean isPrimeByPrimes(int x, List<Integer> existingPrimes) {
        for (int p : existingPrimes) {
            if ((long) p * p > x) break;
            if (x % p == 0) return false;
        }
        return true;
    }
}
