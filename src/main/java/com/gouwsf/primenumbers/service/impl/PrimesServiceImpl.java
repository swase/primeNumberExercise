package com.gouwsf.primenumbers.service.impl;

import com.gouwsf.primenumbers.algorithms.PrimesGenerator;
import com.gouwsf.primenumbers.model.AlgorithmType;
import com.gouwsf.primenumbers.model.PrimeNumberResponse;
import com.gouwsf.primenumbers.service.PrimesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;



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
                .durationNanos(end - start)
                .build();
    }

}
