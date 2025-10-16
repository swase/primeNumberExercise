package com.gouwsf.primenumbers.controller;

import com.gouwsf.primenumbers.api.PrimeNumbersApi;
import com.gouwsf.primenumbers.model.PrimeNumberResponse;
import com.gouwsf.primenumbers.service.PrimesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PrimesController implements PrimeNumbersApi {

    private final PrimesService primesService;

    @GetMapping("/hello")
    public String hello() {
    return "Hello, World, from docker. Adjustment 2!";
    }

    @Override
    public ResponseEntity<PrimeNumberResponse> getPrimeNumbers(Integer n) {
        return ResponseEntity.ok(primesService.getPrimes(n));
    }
}
