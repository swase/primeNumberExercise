package com.gouwsf.primenumbers.controller;

import com.gouwsf.primenumbers.api.PrimeNumbersApi;
import com.gouwsf.primenumbers.model.AlgorithmType;
import com.gouwsf.primenumbers.model.PrimeNumberResponse;
import com.gouwsf.primenumbers.service.PrimesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes endpoints for generating prime numbers.
 * <p>
 * Implements {@link PrimeNumbersApi}, which defines the API contract
 * generated from the OpenAPI specification. Delegates the actual prime
 * number generation to {@link PrimesService}.
 */
@RestController
@RequiredArgsConstructor
public class PrimesController implements PrimeNumbersApi {

    private final PrimesService primesService;

    @Override
    public ResponseEntity<PrimeNumberResponse> getPrimeNumbers(Integer limit, AlgorithmType algo) {
        return ResponseEntity.ok(primesService.generatePrimes(limit, algo));
    }
}
