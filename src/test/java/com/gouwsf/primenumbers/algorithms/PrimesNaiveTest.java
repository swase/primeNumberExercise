package com.gouwsf.primenumbers.algorithms;

import com.gouwsf.primenumbers.algorithms.impl.PrimesNaive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrimesNaiveTest {

    PrimesNaive generator = new PrimesNaive();

    @ParameterizedTest(name = "n={0}")
    @MethodSource("cases")
    @DisplayName("determinePrimes returns primes up to n (inclusive)")
    void determinePrimes_returnsExpected(int n, List<Integer> expected) {
        assertEquals(expected, generator.determinePrimes(n));
    }

    static Stream<Arguments> cases() {
        return Stream.of(
                Arguments.of(1, List.of()),
                Arguments.of(2, List.of(2)),
                Arguments.of(3, List.of(2, 3)),
                Arguments.of(4, List.of(2, 3)),
                Arguments.of(10, List.of(2, 3, 5, 7)),
                Arguments.of(30, List.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)),
                Arguments.of(100, List.of(
                        2, 3, 5, 7, 11, 13, 17, 19, 23, 29,
                        31, 37, 41, 43, 47, 53, 59, 61, 67, 71,
                        73, 79, 83, 89, 97
                ))
        );
    }

    /** Extend Segment Test Cases */
    @DisplayName("extendSegment finds primes in (L,R]")
    @ParameterizedTest(name = "Range ({0},{1}] should yield {2}")
    @MethodSource("segmentCases")
    void testExtendSegment(int fromExclusive, int toInclusive, List<Integer> expectedPrimes) {
        ArrayList<Integer> base = new ArrayList<>(generator.determinePrimes((int)Math.sqrt(toInclusive)));

        generator.extendSegment(fromExclusive, toInclusive, base);

        // Get only primes in (L,R]
        List<Integer> actual = base.stream()
                .filter(p -> p > fromExclusive && p <= toInclusive)
                .toList();

        assertEquals(expectedPrimes, actual);
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> segmentCases() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(0, 10, List.of(2,3,5,7)),
                org.junit.jupiter.params.provider.Arguments.of(10, 20, List.of(11,13,17,19)),
                org.junit.jupiter.params.provider.Arguments.of(20, 30, List.of(23,29))
        );
    }
}