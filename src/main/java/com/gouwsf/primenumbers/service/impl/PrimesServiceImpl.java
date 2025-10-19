package com.gouwsf.primenumbers.service.impl;

import com.gouwsf.primenumbers.algorithms.PrimesGenerator;
import com.gouwsf.primenumbers.model.AlgorithmType;
import com.gouwsf.primenumbers.model.PrimeNumberResponse;
import com.gouwsf.primenumbers.service.PrimesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Default implementation of {@link PrimesService} that delegates prime number
 * generation to different {@link PrimesGenerator} strategies.
 * <p>
 * At application startup, all {@code PrimesGenerator} beans are injected
 * and stored in a lookup map by their {@link AlgorithmType}. This allows
 * clients to request prime numbers using a specific algorithm.
 * <p>
 * Each call to {@link #generatePrimes(int, AlgorithmType)} is timed and
 * returns a {@link PrimeNumberResponse} containing the generated primes,
 * the algorithm used, and the execution duration in nanoseconds.
 */
@Service
public class PrimesServiceImpl implements PrimesService {

    private Map<AlgorithmType, PrimesGenerator> primeGenerators;

    @Autowired
    public PrimesServiceImpl(List<PrimesGenerator> algorithms) {
        this.primeGenerators = algorithms.stream()
                .collect(Collectors.toMap(PrimesGenerator::getType, alg -> alg));
    }

    @Override
    public PrimeNumberResponse generatePrimes(int limit, AlgorithmType algo) {
        return timedResponseWrapper(primeGenerators.get(algo), limit);
    }

    private PrimeNumberResponse timedResponseWrapper(PrimesGenerator generator, int limit) {
        long start, end;
        List<Integer> result;
        start = System.nanoTime();
        try {
            result = generator.determinePrimes(limit);
        } finally {
            end = System.nanoTime();
        }
        return new PrimeNumberResponse.Builder()
                .primes(result)
                .algorithmUsed(generator.getType().name())
                .durationMillis((end - start)/ 1_000_000)
                .numberOfPrimes(result.size())
                .build();
    }

}
