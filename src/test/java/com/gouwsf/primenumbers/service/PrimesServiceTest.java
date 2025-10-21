package com.gouwsf.primenumbers.service;

import com.gouwsf.primenumbers.algorithms.PrimesGenerator;
import com.gouwsf.primenumbers.algorithms.impl.AtkinsSieve;
import com.gouwsf.primenumbers.algorithms.impl.EratosthenesSieve;
import com.gouwsf.primenumbers.algorithms.impl.PrimesNaive;
import com.gouwsf.primenumbers.model.AlgorithmType;
import com.gouwsf.primenumbers.model.PrimeNumberResponse;
import com.gouwsf.primenumbers.service.impl.PrimesExecutorService;
import com.gouwsf.primenumbers.service.impl.PrimesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrimesServiceTest {

    @Mock AtkinsSieve atkins;
    @Mock EratosthenesSieve eratos;
    @Mock PrimesNaive naive;
    @Mock PrimesExecutorService executorService;

    PrimesService service;

    @BeforeEach
    void stubGenerators() {
        when(atkins.getType()).thenReturn(AlgorithmType.ATKIN);
        when(eratos.getType()).thenReturn(AlgorithmType.ERATOS);
        when(naive.getType()).thenReturn(AlgorithmType.NAIVE);

        service = new PrimesServiceImpl(List.of(atkins, eratos, naive), executorService);

        ReflectionTestUtils.setField(service, "ASYNC_LIMIT_START", Integer.MAX_VALUE);
    }

    @DisplayName("generatePrimes uses the correct generator and returns duration")
    @ParameterizedTest(name = "algo={0}, limit={1}")
    @MethodSource("cases")
    void generatePrimes_parameterized_withoutExecutor(AlgorithmType algo, int limit, List<Integer> expected) {
        // Stub only the chosen generator
        when(getChosenMock(algo).determinePrimes(limit)).thenReturn(expected);

        PrimeNumberResponse resp = service.generatePrimes(limit, algo);

        // Verify behaviour
        assertNotNull(resp);
        assertEquals(expected, resp.getPrimes(), "primes list mismatch");
        assertTrue(resp.getDurationMillis() >= 0, "duration should be non-negative");

        verifyCalledOnce(algo, limit);
        verifyNoMoreInteractionsExceptChosen(algo);
        verify(executorService, never()).computeAsync(anyInt(), any(PrimesGenerator.class));
    }

    @DisplayName("generatePrimes uses the correct generator and returns duration")
    @ParameterizedTest(name = "algo={0}, limit={1}")
    @MethodSource("cases")
    void generatePrimes_parameterized_withExecutor(AlgorithmType algo, int limit, List<Integer> expected) {
        ReflectionTestUtils.setField(service, "ASYNC_LIMIT_START", limit - 1);

        // Stub only the chosen generator
        when(executorService.computeAsync(anyInt(), any(PrimesGenerator.class))).thenReturn(expected);

        service.generatePrimes(limit, algo);

        // Verify behaviour
        verify(executorService, times(1)).computeAsync(limit, getChosenMock(algo));
    }

    static Stream<Arguments> cases() {
        return Stream.of(
                // ATKIN
                org.junit.jupiter.params.provider.Arguments.of(
                        AlgorithmType.ATKIN, 10, List.of(2,3,5,7)),
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
            case ATKIN -> atkins;
            case ERATOS  -> eratos;
            case NAIVE  -> naive;
        };
    }

    private void verifyCalledOnce(AlgorithmType algo, int limit) {
        switch (algo) {
            case ATKIN -> {
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
            case ATKIN -> verifyNoMoreInteractions(eratos, naive);
            case ERATOS  -> verifyNoMoreInteractions(atkins, naive);
            case NAIVE  -> verifyNoMoreInteractions(atkins, eratos);
        }
    }
}
