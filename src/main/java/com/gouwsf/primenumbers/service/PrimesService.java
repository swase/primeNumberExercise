package com.gouwsf.primenumbers.service;

import com.gouwsf.primenumbers.model.PrimeNumberResponse;
import com.gouwsf.primenumbers.util.EratosthenesSieve;
import com.gouwsf.primenumbers.util.PrimesNaive;
import org.springframework.stereotype.Service;

@Service
public class PrimesService {

    public PrimeNumberResponse getPrimes(int n) {
        if (n < 1000 ) {
            return new PrimeNumberResponse(PrimesNaive.determinePrimes(n));
        } else {
            return new PrimeNumberResponse(EratosthenesSieve.determinePrimes(n));
        }
    }
}
