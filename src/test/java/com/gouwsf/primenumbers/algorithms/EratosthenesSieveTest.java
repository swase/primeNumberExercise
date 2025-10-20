package com.gouwsf.primenumbers.algorithms;

import com.gouwsf.primenumbers.algorithms.impl.EratosthenesSieve;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EratosthenesSieveTest {

    EratosthenesSieve generator = new EratosthenesSieve();

    @ParameterizedTest(name = "n={0}")
    @MethodSource("cases")
    @DisplayName("determinePrimes returns primes up to n (inclusive)")
    void determinePrimes_returnsExpected(int n, List<Integer> expected) {
        assertEquals(expected, generator.determinePrimes(n));
    }

    static Stream<Arguments> cases() {
        return Stream.of(
                Arguments.of(0, List.of()),
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
    void testDeterminePrimes(int fromExclusive, int toInclusive, List<Integer> base, List<Integer> expectedPrimes) {
        var actual = generator.determinePrimes(fromExclusive, toInclusive, base);
        assertEquals(expectedPrimes, actual);
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> segmentCases() {
        return Stream.of(
                // (fromExclusive, toInclusive, expected primes in (L,R])
                org.junit.jupiter.params.provider.Arguments.of(0, 10, List.of(), List.of(2, 3, 5, 7)),
                org.junit.jupiter.params.provider.Arguments.of(10, 20, List.of(2, 3, 5, 7),List.of(11, 13, 17, 19)),
                org.junit.jupiter.params.provider.Arguments.of(20, 30, List.of(2, 3, 5, 7), List.of(23, 29)),
                org.junit.jupiter.params.provider.Arguments.of(30, 40, List.of(2, 3, 5, 7), List.of(31, 37))
        );
    }
}