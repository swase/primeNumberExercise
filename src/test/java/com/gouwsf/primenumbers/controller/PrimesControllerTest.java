package com.gouwsf.primenumbers.controller;

import com.gouwsf.primenumbers.model.PrimeNumberResponse;
import com.gouwsf.primenumbers.service.PrimesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrimesController.class)
class PrimesControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private PrimesService primesService;

    @Test
    @DisplayName("GET /hello returns greeting")
    void hello_returnsGreeting() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, World, from docker. Adjustment 2!"));
    }

    @Test
    @DisplayName("GET /primeNumbers?n=30 delegates to service and returns 200")
    void getPrimeNumbers_ok() throws Exception {
        int n = 30;
        var response = new PrimeNumberResponse();
        // Adjust these setters/fields to match your actual PrimeNumberResponse
        response.setPrimeNumbers(List.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29));

        Mockito.when(primesService.getPrimes(eq(n))).thenReturn(response);

        mockMvc.perform(get("/primeNumbers").param("n", String.valueOf(n))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        Mockito.verify(primesService).getPrimes(eq(n));
        Mockito.verifyNoMoreInteractions(primesService);
    }

    @Test
    @DisplayName("GET /primeNumbers?n=30 delegates to service and returns 200")
    void getPrimeNumbers_bad_request() throws Exception {
        int n = 0;
        mockMvc.perform(get("/primeNumbers").param("n", String.valueOf(n))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoMoreInteractions(primesService);
    }
}
