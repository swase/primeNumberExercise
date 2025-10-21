package com.gouwsf.primenumbers.integration;

import com.gouwsf.primenumbers.model.AlgorithmType;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PrimesControllerIT {

    @LocalServerPort int port;

    @BeforeAll
    static void enableLoggingOnFailure() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @ParameterizedTest(name = "JSON: /primeNumbers?limit=30&algo={0} -> 200 with primes")
    @EnumSource(value = AlgorithmType.class)
    void primeNumbers_json_allAlgos(AlgorithmType algo) {
        given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("limit", 30)
                .queryParam("algo", algo.name())
                .when()
                .get("/primeNumbers")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("primes", hasItems(2,3,5,7,11,13,17,19,23,29))
                .body("primes.size()", greaterThanOrEqualTo(10))
                .body("durationMillis", greaterThanOrEqualTo(0));
    }

    @Test
    @DisplayName("XML: /primeNumbers?limit=10&algo=NAIVE -> 200 with XML payload")
    void primeNumbers_xml_oneAlgo() {
        given()
            .accept(MediaType.APPLICATION_XML_VALUE)
            .queryParam("limit", 10)
            .queryParam("algo", AlgorithmType.NAIVE.name())
        .when()
            .get("/primeNumbers")
        .then()
            .statusCode(200)
            .contentType(startsWith(MediaType.APPLICATION_XML_VALUE))
            .body("PrimeNumberResponse.durationMillis", notNullValue())
            .body("PrimeNumberResponse.primes.primes.size()", greaterThanOrEqualTo(4))
            .body("PrimeNumberResponse.primes.primes[0]", anyOf(equalTo("2"), equalTo("3")));
    }

    @Test
    @DisplayName("Bad request when limit is invalid (example)")
    void primeNumbers_badRequest_onInvalidLimit() {
        given()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .queryParam("limit", 1)
            .queryParam("algo", AlgorithmType.ERATOS.name())
        .when()
            .get("/primeNumbers")
        .then()
            .statusCode(anyOf(is(400), is(422)));
    }
}
