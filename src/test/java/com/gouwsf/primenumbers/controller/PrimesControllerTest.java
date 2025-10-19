package com.gouwsf.primenumbers.controller;

import com.gouwsf.primenumbers.model.AlgorithmType;
import com.gouwsf.primenumbers.model.PrimeNumberResponse;
import com.gouwsf.primenumbers.service.PrimesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class PrimesControllerTest {


    private PrimesController controller;
    private MockMvc mockMvc;
    @Mock private PrimesService primesService;

    @BeforeEach
    void setup() {
        controller = new PrimesController(primesService);
        mockMvc =  standaloneSetup(controller).build();
    }

    @ParameterizedTest(name = "GET /primeNumbers?limit=30&algo={0} -> 200")
    @EnumSource(AlgorithmType.class)
    void getPrimeNumbers_ok_forEachAlgo(AlgorithmType algo) throws Exception {
        int limit = 30;

        var body = new PrimeNumberResponse.Builder()
                .primes(List.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29))
                .durationMillis(1234L)
                .build();

        Mockito.when(primesService.generatePrimes(eq(limit), eq(algo)))
                .thenReturn(body);

        mockMvc.perform(get("/primeNumbers")
                        .param("limit", String.valueOf(limit))
                        .param("algo", algo.name())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.primes[0]").value(2))
                .andExpect(jsonPath("$.primes.length()").value(10))
                .andExpect(jsonPath("$.durationMillis").value(1234));

        Mockito.verify(primesService).generatePrimes(eq(limit), eq(algo));
        Mockito.verifyNoMoreInteractions(primesService);
    }

    @Test
    void getPrimeNumbers_bad_request_lessThan2() throws Exception {
        int n = 0;
        mockMvc.perform(get("/primeNumbers").param("n", String.valueOf(n))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoMoreInteractions(primesService);
    }

    @Test
    void getPrimeNumbers_bad_request_higherThanMax() throws Exception {
        int n = 252_000_000;
        mockMvc.perform(get("/primeNumbers").param("n", String.valueOf(n))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoMoreInteractions(primesService);
    }
}
