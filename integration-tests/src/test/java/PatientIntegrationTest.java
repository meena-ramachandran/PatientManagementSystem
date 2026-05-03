import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;


import io.restassured.RestAssured;

public class PatientIntegrationTest {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://localhost:4007"; // API Gateway URL
    }

    @Test
    public void shouldReturnPatientsWithValidToken() {
        // 1. Arrange
        String loginPayload = """
        {   
            "email": "testuser@test.com", 
            "password": "password123"
        }
        """;

        String token = given()
            .contentType("application/json")
            .body(loginPayload)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getString("token");

        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/api/patients")
            .then()
            .statusCode(200)
            .body("patients", notNullValue());
    }
}
        
