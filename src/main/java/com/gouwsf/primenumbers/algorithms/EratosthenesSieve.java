package com.gouwsf.primenumbers.algorithms;

import com.gouwsf.primenumbers.model.AlgorithmType;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Getter
public class EratosthenesSieve implements PrimesGenerator {

    private final AlgorithmType type = AlgorithmType.ERATOS;

    public List<Integer> determinePrimes(int n) {
        boolean[] sieve = new boolean[n + 1];
        Arrays.fill(sieve, true);

        for (int p = 2; p * p <= n; p++) {
            if(sieve[p]) {
                for (int j = p * p; j <= n; j += p) {
                    sieve[j] = false;
                }
            }
        }
        return convertSieveToList(sieve);
    }
}
