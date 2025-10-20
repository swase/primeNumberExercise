package com.gouwsf.primenumbers.service;

import com.gouwsf.primenumbers.algorithms.PrimesGenerator;
import com.gouwsf.primenumbers.service.impl.PrimesAsyncExecutorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrimesAsyncExecutorServiceTest {

    @Mock
    PrimesGenerator generator;

    ExecutorService executor;

    PrimesAsyncExecutorService service;

    @BeforeEach
    void setUp() {
        // bounded pool to avoid flakiness; 4 threads is fine for tests
        executor = Executors.newFixedThreadPool(4);
        service = new PrimesAsyncExecutorService(executor);
    }

    @AfterEach
    void tearDown() {
        executor.shutdownNow();
    }

    @Test
    void returnsEmptyForLimitLessThan2_andDoesNotCallGenerator() {
        List<Integer> result = service.computeAsync(1, generator).join();

        assertThat(result).isEmpty();
        verify(generator, never()).determinePrimes(anyInt());                // base primes not computed
        verify(generator, never()).determinePrimes(anyInt(), anyInt(), any()); // no segment calls
    }

    @Test
    void usesAtMostTenSegments_andCallsGeneratorOncePerSegment() {
        // Arrange
        int limit = 1_000_002; // big enough to ensure 10 segments
        int root = (int) Math.floor(Math.sqrt(limit));
        when(generator.determinePrimes(root)).thenReturn(List.of(2,3,5,7,11,13,17,19,23,29,31)); // dummy base primes
        // for each segment call just return lo..hi primes dummy (we don't assert primality here)
        when(generator.determinePrimes(anyInt(), anyInt(), any()))
                .thenAnswer(inv -> {
                    int lo = inv.getArgument(0, Integer.class);
                    int hi = inv.getArgument(1, Integer.class);
                    // return a small marker list so we can verify coverage later
                    return List.of(lo, hi);
                });

        // Act
        List<Integer> merged = service.computeAsync(limit, generator).join();

        // Assert: base primes computed once with sqrt(limit)
        verify(generator, times(1)).determinePrimes(root);

        // Assert: 10 segment invocations
        verify(generator, times(10)).determinePrimes(anyInt(), anyInt(), any());

        // Rebuild expected segments locally (same logic as service)
        List<int[]> expectedSegments = buildSegmentsLocal(2, limit, 10);

        // Merged list is concatenation of each [lo, hi] pair in segment order
        List<Integer> expectedMerged = expectedSegments.stream()
                .flatMap(seg -> List.of(seg[0], seg[1]).stream())
                .collect(Collectors.toList());

        assertThat(merged).isEqualTo(expectedMerged);
    }

    @Test
    void preservesSegmentOrder_evenIfTasksFinishOutOfOrder() {
        int limit = 200_000; // small but enough segments; will produce 10 segments
        int root = (int) Math.floor(Math.sqrt(limit));
        when(generator.determinePrimes(root)).thenReturn(List.of(2,3,5,7,11,13));

        // Return [lo, hi] but sleep inversely to 'lo' to scramble completion
        when(generator.determinePrimes(anyInt(), anyInt(), any()))
                .thenAnswer(inv -> {
                    int lo = inv.getArgument(0, Integer.class);
                    int hi = inv.getArgument(1, Integer.class);
                    // bigger 'lo' sleeps less -> later segments finish earlier
                    try { Thread.sleep(Math.max(0, 50 - (lo % 50))); } catch (InterruptedException ignored) {}
                    return List.of(lo, hi);
                });

        List<Integer> merged = service.computeAsync(limit, generator).join();

        // Expected order is the *submission/segment order*, not completion order.
        List<int[]> expectedSegments = buildSegmentsLocal(2, limit, 10);
        List<Integer> expectedMerged = expectedSegments.stream()
                .flatMap(seg -> List.of(seg[0], seg[1]).stream())
                .collect(Collectors.toList());

        assertThat(merged).isEqualTo(expectedMerged);
    }

    @Test
    void computesBasePrimesUpToFloorSqrtLimitExactlyOnce() {
        int limit = 100;    // sqrt = 10
        when(generator.determinePrimes(10)).thenReturn(List.of(2,3,5,7));
        when(generator.determinePrimes(anyInt(), anyInt(), any()))
                .thenReturn(List.of(2,3)); // minimal dummy

        service.computeAsync(limit, generator).join();

        verify(generator, times(1)).determinePrimes(10);
        verifyNoMoreInteractions(generator);
    }

    // ---- test helper: same segmentation as service (copy) ------------------
    private static List<int[]> buildSegmentsLocal(int start, int end, int maxSegments) {
        List<int[]> out = new ArrayList<>();
        long total = (long) end - start + 1;
        int segments = (int) Math.min(maxSegments, Math.max(1, total));
        long baseSize = total / segments;
        long remainder = total % segments;

        int lo = start;
        for (int i = 0; i < segments; i++) {
            long size = baseSize + (i < remainder ? 1 : 0);
            int hi = (int) (lo + size - 1);
            out.add(new int[]{lo, hi});
            lo = hi + 1;
        }
        return out;
    }
}
