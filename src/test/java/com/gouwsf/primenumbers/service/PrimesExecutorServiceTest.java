package com.gouwsf.primenumbers.service.impl;

import com.gouwsf.primenumbers.algorithms.PrimesGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class PrimesExecutorServiceTest {

    private final ExecutorService pool = Executors.newFixedThreadPool(4);

    @AfterEach
    void tearDown() {
        pool.shutdownNow();
    }

    @Test
    void computeAsync_limitBelow2_returnsEmpty_andNoGeneratorCalls() {
        // given
        PrimesGenerator generator = mock(PrimesGenerator.class);
        PrimesExecutorService svc = new PrimesExecutorService(pool);

        // when
        List<Integer> out = svc.computeAsync(1, generator);

        // then
        assertTrue(out.isEmpty());
        verifyNoInteractions(generator);
    }

    @Test
    void computeAsync_orchestratesBaseAndSegments_andFlattensInOrder() {
        // given
        int limit = 10;                    // sqrt(10) = 3
        int root = (int) Math.floor(Math.sqrt(limit));
        var basePrimes = List.of(2, 3);    // arbitrary; only used as a token to pass through

        PrimesGenerator generator = mock(PrimesGenerator.class);

        // Base primes up to sqrt(limit) called once
        when(generator.determinePrimes(root)).thenReturn(basePrimes);

        // For each segment call, just return a singleton list containing the segment's hi,
        // so we can assert flattening + ordering deterministically without caring about primality.
        when(generator.determinePrimes(anyInt(), anyInt(), anyList()))
                .thenAnswer(inv -> List.of(inv.getArgument(1, Integer.class))); // return [toInclusive]

        PrimesExecutorService svc = new PrimesExecutorService(pool);

        // when
        List<Integer> out = svc.computeAsync(limit, generator);

        // then: base call once with root
        verify(generator, times(1)).determinePrimes(root);

        // capture segment calls
        ArgumentCaptor<Integer> from = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> to = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<List<Integer>> base = ArgumentCaptor.forClass(List.class);
        verify(generator, atLeastOnce()).determinePrimes(from.capture(), to.capture(), base.capture());

        // all segment calls must receive the SAME basePrimes instance returned from the base call
        base.getAllValues().forEach(bp -> assertSame(basePrimes, bp));

        // segments should cover [2..limit] with consecutive, non-overlapping ranges built by buildSegments
        int globalMin = from.getAllValues().stream().mapToInt(v -> v + 1).min().orElseThrow();
        int globalMax = to.getAllValues().stream().mapToInt(Integer::intValue).max().orElseThrow();
        assertEquals(2, globalMin);
        assertEquals(limit, globalMax);

        // because limit=10 and MAX_SEGMENTS=10, buildSegments will create 9 one-size segments for [2..10],
        // and our answer function returns [hi] per segment; so the flattened list should be [2..10]
        assertEquals(List.of(2, 3, 4, 5, 6, 7, 8, 9, 10), out);
    }
}
