package ru.tatarinov.NauJava.controllers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookCustomControllerTest {

    @LocalServerPort
    private int port;

    private String authToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        authToken = given()
                .contentType(ContentType.URLENC)
                .formParam("username", "admin")
                .formParam("password", "admin")
                .when()
                .post("/login")
                .then()
                .statusCode(302)
                .extract()
                .cookie("JSESSIONID");
    }

    @Test
    void searchBooks_ShouldReturnBooks() {
        given()
                .cookie("JSESSIONID", authToken)
                .queryParam("title", "Java")
                .queryParam("startYear", "2000")
                .queryParam("endYear", "2015")
                .when()
                .get("/api/custom/books/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("[]"));
    }

    @Test
    void getByCountry_InvalidCountry_ShouldReturnEmptyList() {
        given()
                .cookie("JSESSIONID", authToken)
                .pathParam("country", "Nonexistent")
                .when()
                .get("/api/custom/books/by-country/{country}")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("[]"));
    }
}
