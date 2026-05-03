import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AuthIntegrationTest {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://localhost:4007"; // API Gateway URL
    }

    @Test
    public void shouldReturnOKWithValidToken() {
        // 1. Arrange
        String loginPayload = """
        {   
            "email": "testuser@test.com", 
            "password": "password123"
        }
        """;
        // 2. Act

        Response response = given()
            .contentType("application/json")
            .body(loginPayload)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .body("token", notNullValue())
            .extract()
            .response();
        // 3. Assert

        System.out.print("Generated Token: " + response.jsonPath().getString("token"));
    }

    @Test
    public void shouldReturnUnauthorizedWithInvalidToken() {
        // 1. Arrange
        String loginPayload = """
        {   
            "email": "invaliduser@test.com", 
            "password": "wrongpassword"
        }
        """;
        // 2. Act

            given().contentType("application/json")
            .body(loginPayload)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(401);
        // 3. Assert

        System.out.print("Unauthorized access with invalid credentials");
    }
}
