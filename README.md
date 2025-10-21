# PrimesAPI
A simple Spring Boot REST API that generates prime numbers up to (and including) a given input.

### 🔗 Live
- **Render**: https://primenumberexercise.onrender.com
- **Swagger UI**: https://primenumberexercise.onrender.com/swagger-ui/index.html

> ⚠️ Note: This runs on Render’s free tier. The service may “sleep.” The first request after sleep can take **> 1 minute** to wake up.
---

## Project Set-up
```
git clone git@github.com:swase/primeNumberExercise.git

cd primeNumberExercise

mvn clean install

mvn spring-boot:run
```

---
## 🧠 Overview

There are **three algorithms** implemented, which can be chosen at runtime via the `algo` query parameter.  
The default is **Sieve of Eratosthenes**.

- `NAIVE` – Optimised naive approach
- `ERATOS` – Sieve of Eratosthenes (default)
- `ATKIN` – Sieve of Atkin

Responses can be returned in **XML** or **JSON** depending on the `Accept` header (`application/xml` or `application/json`).  
For XML, the response is **wrapped** for better structure.

---

### ⚙️ Considerations

- **Input range**: Minimum input is `2` (lowest prime). Maximum input is `250,000,000`.  
  This limit is set due to RAM constraints on the deployment environment and the space complexity of sieve algorithms.
- **Swagger UI**: Executing large queries via Swagger is not practical (UI may hang).  
  For large limits, use `curl` or a direct HTTP client.
- **Render free tier**: The deployment goes into *sleep mode* after inactivity.  
  First request after sleep can take **> 1 minute**. Accessing Swagger or `/primeNumbers` wakes the service automatically.

---

### 🗄️ Cache

- Implemented with **Caffeine (in-memory)**.
- **Cache key**: combination of `algo` and `limit`.
- **Cache value**: full response body (so that `algorithmUsed` and `durationMillis` are preserved).
- **Expiry**: entries expire after **2 hours** or when cache size reaches ~**100 MB**.

> ⚡️ Note: This approach is straightforward but not the most memory-efficient.  
> A possible optimization would be to maintain a **single expandable list of primes**, extending it as requests for higher limits arrive.

---

### 🧵 Concurrent Execution

- A **thread pool** is created at startup (size = number of CPU cores).
- For `limit ≥ 500,000`, prime generation uses **segmented concurrent execution**.
- For smaller requests, execution runs on a **single thread**.
- Current cutoff (`500,000`) is a **hard-coded service-layer limit**, but it could be made configurable.

---

## API Endpoints

### `/primeNumbers`
Generates all prime numbers up to a given limit, using a chosen algorithm.

### Query Parameters

| Name   | Type                         | Required    | Default  | Description                                                                          |
|--------|------------------------------|-------------|----------|--------------------------------------------------------------------------------------|
| `limit` | `integer`                    | ✅ Yes       | –        | Upper bound (inclusive). Must be ≥ 2. and <= 250_000_000                             |
| `algo`  | `AlgorithmType` (enum)       | ❌ No        | `ERATOS` | Algorithm to use for prime generation. Supported values: `NAIVE`, `ERATOS`, `ATKIN`. |

### Response

Returns a `PrimeNumberResponse` object.

| Field           | Type      | Description                                            |
|-----------------|-----------|--------------------------------------------------------|
| `primeNumbers`  | `array`   | The list of prime numbers up to `limit`.               |
| `algorithmUsed` | `string`  | The algorithm applied (`NAIVE`, `ERATOS`, or `ATKIN`). |
| `durationMillis` | `integer` | Time taken to compute, in milli-seconds.               |
| `numberOfPrimes` | `integer` | Total number of primes generated.                      | 

### Examples

#### XML
```bash
curl "https://primenumberexercise.onrender.com/primeNumbers?limit=10&algo=ERATOS" \
  -H "Accept: application/xml"
```
```xml
<APIResponse>
    <algorithmUsed>ERATOS</algorithmUsed>
    <primes>
      <primes>2</primes>
      <primes>3</primes>
      <primes>5</primes>
      <primes>7</primes>
      <primes>11</primes>
    </primes>
    <durationNanos>1</durationNanos>
</APIResponse>
```

#### JSON
```bash
curl "https://primenumberexercise.onrender.com/primeNumbers?limit=10&algo=ERATOS" \
  -H "Accept: application/json"
```

```json
{
    "algorithmUsed": "ERATOS",
    "primes": [2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97],
    "durationNanos": 3000
}
```
---

#### ErrorResponse: JSON
```json
{
  "status": 400,
  "error": "Bad Request",
  "title": "Validation Failed",
  "description": "Parameter 'n' must be greater than 0"
}
```

#### ErrorResponse: XML
```xml
<ErrorResponse>
  <status>400</status>
  <error>Bad Request</error>
  <title>Validation Failed</title>
  <description>Parameter 'n' must be greater than 0</description>
</ErrorResponse>
```

## Time and Space Complexity

| Algorithm             | Time Complexity  | Space Complexity | Overview                                                                  |
|-----------------------|------------------|------------------|---------------------------------------------------------------------------|
| Naive Approach        | O(N * sqrt(N))   | O(N/log(N)))     | Good for Verification of small inputs.                                    |
| Sieve of Eratosthenes | O(N * loglog(N)) | O(N)             | Fastest for most practical ranges (up to 10¹²) due to simplicity.         |
| Sieve of Atkin        | O(N)             | O(N)             | More complex than Eratosthenes - can be less efficient for primes <  10¹⁸ |

---

## Technologies Used

- Java 17
- Maven
- Spring Boot
- JUnit 5
- RestAssured
- Open API Specification and maven Open API Generator
- Markdown + HTML rendering
- Docker
- Lombok
- Jackson Databind
---

## 📬 Contact
For any info please contact myself, Francois. email: francois.gouws@natwest.com
