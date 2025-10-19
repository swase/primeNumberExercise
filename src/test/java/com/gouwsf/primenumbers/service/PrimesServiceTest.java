package com.gouwsf.primenumbers.service;

import com.gouwsf.primenumbers.algorithms.AtkinsSieve;
import com.gouwsf.primenumbers.algorithms.EratosthenesSieve;
import com.gouwsf.primenumbers.algorithms.PrimesGenerator;
import com.gouwsf.primenumbers.algorithms.PrimesNaive;
import com.gouwsf.primenumbers.model.AlgorithmType;
import com.gouwsf.primenumbers.model.PrimeNumberResponse;
import com.gouwsf.primenumbers.service.impl.PrimesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrimesServiceTest {

    @Mock AtkinsSieve atkins;
    @Mock EratosthenesSieve eratos;
    @Mock PrimesNaive naive;

    @BeforeEach
    void stubGenerators() {
        when(atkins.getType()).thenReturn(AlgorithmType.ATKINS);
        when(eratos.getType()).thenReturn(AlgorithmType.ERATOS);
        when(naive.getType()).thenReturn(AlgorithmType.NAIVE);
    }

    @DisplayName("generatePrimes uses the correct generator and returns duration")
    @ParameterizedTest(name = "algo={0}, limit={1}")
    @MethodSource("cases")
    void generatePrimes_parameterized(AlgorithmType algo, int limit, List<Integer> expected) {
        // Stub only the chosen generator
        when(getChosenMock(algo).determinePrimes(anyInt())).thenReturn(expected);

        var service = new PrimesServiceImpl(List.of(atkins, eratos, naive));

        PrimeNumberResponse resp = service.generatePrimes(limit, algo);

        // Verify behaviour
        assertNotNull(resp);
        assertEquals(expected, resp.getPrimes(), "primes list mismatch");
        assertTrue(resp.getDurationNanos() >= 0, "duration should be non-negative");

        verifyCalledOnce(algo, limit);
        verifyNoMoreInteractionsExceptChosen(algo);
    }

    // Data
    static Stream<Arguments> cases() {
        return Stream.of(
                // ATKINS
                org.junit.jupiter.params.provider.Arguments.of(
                        AlgorithmType.ATKINS, 10, List.of(2,3,5,7)),
                // ERATOS
                org.junit.jupiter.params.provider.Arguments.of(
                        AlgorithmType.ERATOS, 20, List.of(2,3,5,7,11,13,17,19)),
                // NAIVE
                org.junit.jupiter.params.provider.Arguments.of(
                        AlgorithmType.NAIVE, 30, List.of(2,3,5,7,11,13,17,19,23,29))
        );
    }

    /* --- helpers --- */
    private PrimesGenerator getChosenMock(AlgorithmType algo) {
        return switch (algo) {
            case ATKINS -> atkins;
            case ERATOS  -> eratos;
            case NAIVE  -> naive;
        };
    }

    private void verifyCalledOnce(AlgorithmType algo, int limit) {
        switch (algo) {
            case ATKINS -> {
                verify(atkins, times(1)).determinePrimes(limit);
                verify(eratos, never()).determinePrimes(anyInt());
                verify(naive, never()).determinePrimes(anyInt());
            }
            case ERATOS -> {
                verify(eratos, times(1)).determinePrimes(limit);
                verify(atkins, never()).determinePrimes(anyInt());
                verify(naive, never()).determinePrimes(anyInt());
            }
            case NAIVE -> {
                verify(naive, times(1)).determinePrimes(limit);
                verify(atkins, never()).determinePrimes(anyInt());
                verify(eratos, never()).determinePrimes(anyInt());
            }
        }
    }

    private void verifyNoMoreInteractionsExceptChosen(AlgorithmType algo) {
        switch (algo) {
            case ATKINS -> verifyNoMoreInteractions(eratos, naive);
            case ERATOS  -> verifyNoMoreInteractions(atkins, naive);
            case NAIVE  -> verifyNoMoreInteractions(atkins, eratos);
        }
    }
}
