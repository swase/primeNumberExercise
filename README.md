# PrimesAPI

### 🔗 [Render Deployment](https://primenumberexercise.onrender.) : https://primenumberexercise.onrender.com
### 🔗 [Swagger Link](https://primenumberexercise.onrender.com/swagger-ui/index.html)
A simple SpringBoot RESTful application used to generate prime numbers up to and including a given input.

---

## Project Set-up
```
git clone git@github.com:swase/primeNumberExercise.git

cd primeNumberExercise

mvn clean install

mvn spring-boot:run
```

---
## Overview
There are 3 algorithms that are implemented and can be chosen at runtime via query param. Default being Sieve of Eratosthenes.
The algorithms chosen are Optimised Naive approach, Sieve of Eratosthenes and Atkins Sieve.

XML or JSON response can also be chosen using headers 'application/xml' or 'application/json' respectively. For XML I have chosen to wrap XML response.

### Some Considerations
The minimum input is 2 (lowest prime) and max is 250_000_000. Considering limited RAM on chosen deployment and space complexity of sieve algorithms, the max limit was chosen.

Using Swagger to execute the query for large numbers is not practical and may struggle to load. For larger numbers its best to use curl cmd.

As the service is deployed on free tier, Render will put deployment in 'sleep' mode after a while. This will increase initial request to > 1min. Hitting the swagger or primes end point will automatically wake up the service.

#### Cache
Cache is enabled using Caffiene (in memory) using 'algo' and 'limit' as key and response body as value. I've chosen this approach (admittedly not as efficient), as I wish to return ALGO type and execution duration. 

Further optimisation could have been made by customising how list of primes is stored. Storing rather a single list of primes that is extended as limit is increased on incoming request.

Cache will expire after 2hours or when cache reaches ~100mb.

#### Concurrent Execution
I've implemented algorithm in such a way as to allow for concurrent execution. Thread pool is created at startup and maxes out at number of cores. 

Concurrent execution is set to start after an upper limit of 500_000 (otherwise will be single thread). This is a hard limit at service layer. Although you could easily be switched out for configurable cutoff.

## API Endpoints

### `/primeNumbers`
Generates all prime numbers up to a given limit, using a chosen algorithm.

### Query Parameters

| Name   | Type                         | Required    | Default  | Description                                                                           |
|--------|------------------------------|-------------|----------|---------------------------------------------------------------------------------------|
| `limit` | `integer`                    | ✅ Yes       | –        | Upper bound (inclusive). Must be ≥ 2. and <= 250_000_000                              |
| `algo`  | `AlgorithmType` (enum)       | ❌ No        | `ERATOS` | Algorithm to use for prime generation. Supported values: `NAIVE`, `ERATOS`, `ATKINS`. |

### Response

Returns a `PrimeNumberResponse` object.

| Field           | Type      | Description                                             |
|-----------------|-----------|---------------------------------------------------------|
| `primeNumbers`  | `array`   | The list of prime numbers up to `limit`.                |
| `algorithmUsed` | `string`  | The algorithm applied (`NAIVE`, `ERATOS`, or `ATKINS`). |
| `durationMillis` | `integer` | Time taken to compute, in milli-seconds.                |
| `numberOfPrimes` | `integer` | Total number of primes generated.                       | 

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
