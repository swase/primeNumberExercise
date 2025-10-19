package com.gouwsf.primenumbers.algorithms;

import com.gouwsf.primenumbers.algorithms.impl.PrimesNaive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
}