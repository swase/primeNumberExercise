package com.gouwsf.primenumbers.service;

import com.gouwsf.primenumbers.model.PrimeNumberResponse;
import com.gouwsf.primenumbers.util.PrimesNaive;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrimesService {

    public PrimeNumberResponse getPrimes(int limit) {
        return new PrimeNumberResponse(PrimesNaive.getPrimes(limit));
    }
}
