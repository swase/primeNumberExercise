package com.gouwsf.primenumbers.service;

import com.gouwsf.primenumbers.model.AlgorithmType;
import com.gouwsf.primenumbers.model.PrimeNumberResponse;

public interface PrimesService {
    PrimeNumberResponse generatePrimes(int limit, AlgorithmType algo);
}
