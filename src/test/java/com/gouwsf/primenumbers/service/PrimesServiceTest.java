package com.gouwsf.primenumbers.service;

import com.gouwsf.primenumbers.model.PrimeNumberResponse;
import com.gouwsf.primenumbers.util.EratosthenesSieve;
import com.gouwsf.primenumbers.util.PrimesNaive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrimesServiceStaticMockTest {

    private final PrimesService service = new PrimesService();

    @Test
    @DisplayName("Delegates to PrimesNaive when n < 1000")
    void delegatesToNaive() {
        int n = 50;
        List<Integer> fake = List.of(2, 3, 5);

        try (MockedStatic<PrimesNaive> naiveMock = Mockito.mockStatic(PrimesNaive.class);
             MockedStatic<EratosthenesSieve> sieveMock = Mockito.mockStatic(EratosthenesSieve.class)) {

            naiveMock.when(() -> PrimesNaive.determinePrimes(n)).thenReturn(fake);

            PrimeNumberResponse resp = service.getPrimes(n);

            assertEquals(fake, resp.getPrimeNumbers());

            // verify called once with n
            naiveMock.verify(() -> PrimesNaive.determinePrimes(n), Mockito.times(1));
            // ensure sieve not touched
            sieveMock.verifyNoInteractions();
        }
    }

    @Test
    @DisplayName("Delegates to EratosthenesSieve when n >= 1000")
    void delegatesToSieve() {
        int n = 1200;
        List<Integer> fake = List.of(2, 3, 5, 7);

        try (MockedStatic<EratosthenesSieve> sieveMock = Mockito.mockStatic(EratosthenesSieve.class);
             MockedStatic<PrimesNaive> naiveMock = Mockito.mockStatic(PrimesNaive.class)) {

            sieveMock.when(() -> EratosthenesSieve.determinePrimes(n)).thenReturn(fake);

            PrimeNumberResponse resp = service.getPrimes(n);

            assertEquals(fake, resp.getPrimeNumbers());

            // verify called once with n
            sieveMock.verify(() -> EratosthenesSieve.determinePrimes(n), Mockito.times(1));
            // ensure naive not touched
            naiveMock.verifyNoInteractions();
        }
    }
}
