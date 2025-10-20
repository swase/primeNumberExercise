package com.gouwsf.primenumbers.algorithms;

import com.gouwsf.primenumbers.algorithms.impl.AtkinsSieve;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AtkinsSieveParameterizedTest {

    private final AtkinsSieve sieve = new AtkinsSieve();

    @ParameterizedTest(name = "limit={0} -> {1}")
    @MethodSource("cases")
    void determinesPrimes_forVariousLimits(int limit, List<Integer> expected) {
        List<Integer> actual = sieve.determinePrimes(limit);
        assertEquals(expected, actual,
                () -> "Unexpected primes for limit=" + limit + ", got=" + actual);
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> cases() {
        return Stream.of(
            // edge cases
            org.junit.jupiter.params.provider.Arguments.of(0, List.of()),
            org.junit.jupiter.params.provider.Arguments.of(1, List.of()),
            org.junit.jupiter.params.provider.Arguments.of(2, List.of(2)),
            org.junit.jupiter.params.provider.Arguments.of(3, List.of(2, 3)),
            // small ranges
            org.junit.jupiter.params.provider.Arguments.of(10, List.of(2, 3, 5, 7)),
            org.junit.jupiter.params.provider.Arguments.of(20, List.of(2, 3, 5, 7, 11, 13, 17, 19)),
            // a bit larger
            org.junit.jupiter.params.provider.Arguments.of(30, List.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)),
            org.junit.jupiter.params.provider.Arguments.of(50, List.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47))
        );
    }
    /* ------------------------------  Extend Segment Method Test Cases ---------------------------------------------- */

    @DisplayName("extendSegment finds primes in (L,R]")
    @ParameterizedTest(name = "Range ({0},{1}] → {2}")
    @MethodSource("segmentCases")
    void testExtendSegment(int fromExclusive, int toInclusive, List<Integer> expectedPrimes) {
        // Seed basePrimes with primes up to sqrt(toInclusive)
        ArrayList<Integer> base = new ArrayList<>(sieve.determinePrimes((int) Math.sqrt(toInclusive)));

        sieve.extendSegment(fromExclusive, toInclusive, base);

        // Extract only primes in (fromExclusive, toInclusive]
        List<Integer> actual = base.stream()
                .filter(p -> p > fromExclusive && p <= toInclusive)
                .toList();

        assertEquals(expectedPrimes, actual);
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> segmentCases() {
        return Stream.of(
                // (fromExclusive, toInclusive, expected primes in (L,R])
                org.junit.jupiter.params.provider.Arguments.of(0, 10, List.of(2, 3, 5, 7)),
                org.junit.jupiter.params.provider.Arguments.of(10, 20, List.of(11, 13, 17, 19)),
                org.junit.jupiter.params.provider.Arguments.of(20, 30, List.of(23, 29)),
                org.junit.jupiter.params.provider.Arguments.of(30, 40, List.of(31, 37))
        );
    }
}
