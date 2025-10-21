package com.gouwsf.primenumbers.service.impl;

import com.gouwsf.primenumbers.algorithms.PrimesGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PrimesExecutorService {

    private static final int MAX_SEGMENTS = 10;
    private final ExecutorService executor;


    /**
     * Asynchronously computes all primes ≤ limit using at most MAX_SEGMENTS segments.
     * Start with basePrimes up to square root of limit.
     *
     * @param limit upper bound limit - inclusivea
     * @param generator chosen primes generator (based on per implementation basis)
     */
    public List<Integer> computeAsync(int limit, PrimesGenerator generator) {
        if (limit < 2) return List.of();

        // Compute base primes - only need to go to sqrt(limit)
        int root = (int) Math.floor(Math.sqrt(limit));
        var basePrimes = generator.determinePrimes(root);

        // build segments
        List<Segment> segments = buildSegments(2, limit, MAX_SEGMENTS);

        // wrap generator function inside completable future
        CompletableFuture<List<Integer>>[] futures = new CompletableFuture[segments.size()];
        int i = 0;
        for (Segment segment: segments) {
//            var tmp = generator.determinePrimes(segment.low() - 1, segment.hi(), basePrimes);
            futures[i] = CompletableFuture.supplyAsync(
                    () -> generator.determinePrimes(segment.low() - 1, segment.hi(), basePrimes),
                    executor
            );
            i++;
        }


        return Stream.of(futures)
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Helper method used to build segments
     */
    private static List<Segment> buildSegments(int start, int end, int maxSegments) {
        List<Segment> out = new ArrayList<>(maxSegments);
        long total = (long) end - start + 1;
        int segments = (int) Math.min(maxSegments, Math.max(1, total));
        long baseSize = total / segments;
        long remainder = total % segments;

        int low = start;
        for (int i = 0; i < segments; i++) {
            long size = baseSize + (i < remainder ? 1 : 0);
            int high = (int) (low + size - 1);
            out.add(new Segment(low, high));
            low = high + 1;
        }
        return out;
    }

    /** Helper record for low, high range*/
    private record Segment(int low, int hi) {}
}
