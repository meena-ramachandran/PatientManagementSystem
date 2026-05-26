import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.UUID;

public class DoctorAndConflictIntegrationTest {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://localhost:4007"; // API Gateway URL
    }

    @Test
    public void shouldRegisterDoctorAndDetectAppointmentConflicts() {
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        String doctorEmail = "doctor." + randomSuffix + "@hospital.org";
        String doctorPassword = "password123";

        // 1. Register a new Doctor (Public Endpoint)
        String registerDoctorPayload = String.format("""
        {
            "name": "Dr. House %s",
            "specialization": "Diagnostic Medicine",
            "email": "%s",
            "password": "%s",
            "phone": "+1-555-1029"
        }
        """, randomSuffix, doctorEmail, doctorPassword);

        Response regResponse = given()
            .contentType("application/json")
            .body(registerDoctorPayload)
            .when()
            .post("/auth/register-doctor")
            .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("userId", notNullValue())
            .body("email", equalTo(doctorEmail))
            .body("specialization", equalTo("Diagnostic Medicine"))
            .extract()
            .response();

        String doctorId = regResponse.jsonPath().getString("id");
        String doctorUserId = regResponse.jsonPath().getString("userId");

        // 2. Log in as ADMIN to obtain token for patient/appointment CRUD
        String loginPayload = """
        {   
            "email": "testuser@test.com", 
            "password": "password123"
        }
        """;

        String adminToken = given()
            .contentType("application/json")
            .body(loginPayload)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getString("token");

        // 2b. Log in as the registered Doctor to get a PHYSICIAN token
        String doctorLoginPayload = String.format("""
        {   
            "email": "%s", 
            "password": "%s"
        }
        """, doctorEmail, doctorPassword);

        String doctorToken = given()
            .contentType("application/json")
            .body(doctorLoginPayload)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getString("token");

        // 2c. Verify RBAC rules: PHYSICIAN is forbidden from accessing /api/users (restricted to ADMIN)
        given()
            .header("Authorization", "Bearer " + doctorToken)
            .when()
            .get("/api/users")
            .then()
            .statusCode(403);

        // 2d. Verify RBAC rules: PHYSICIAN is allowed to access /api/patients
        given()
            .header("Authorization", "Bearer " + doctorToken)
            .when()
            .get("/api/patients")
            .then()
            .statusCode(200);

        // 3. Register a new Patient
        String patientEmail = "patient." + randomSuffix + "@example.com";
        String registerPatientPayload = String.format("""
        {
            "name": "Intake Patient %s",
            "email": "%s",
            "address": "456 Baker St",
            "dateOfBirth": "1988-11-30",
            "registeredDate": "2026-05-26"
        }
        """, randomSuffix, patientEmail);

        String patientId = given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType("application/json")
            .body(registerPatientPayload)
            .when()
            .post("/api/patients")
            .then()
            .statusCode(200)
            .body("id", notNullValue())
            .extract()
            .jsonPath()
            .getString("id");

        // 4. Register a second Patient (for checking overlapping doctor schedule with different patients)
        String patient2Email = "patient2." + randomSuffix + "@example.com";
        String registerPatient2Payload = String.format("""
        {
            "name": "Second Patient %s",
            "email": "%s",
            "address": "789 Elm St",
            "dateOfBirth": "1994-06-15",
            "registeredDate": "2026-05-26"
        }
        """, randomSuffix, patient2Email);

        String patient2Id = given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType("application/json")
            .body(registerPatient2Payload)
            .when()
            .post("/api/patients")
            .then()
            .statusCode(200)
            .body("id", notNullValue())
            .extract()
            .jsonPath()
            .getString("id");

        // 5. Book first appointment (Patient 1 with Doctor at 10:00 AM, duration 30m)
        String apptDateTime1 = "2026-06-15T10:00:00";
        String apptPayload1 = String.format("""
        {
            "patientId": "%s",
            "userId": "%s",
            "appointmentDateTime": "%s",
            "durationMinutes": 30,
            "appointmentFee": 100.00,
            "status": "SCHEDULED",
            "notes": "First checkup"
        }
        """, patientId, doctorUserId, apptDateTime1);

        String apptId1 = given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType("application/json")
            .body(apptPayload1)
            .when()
            .post("/api/appointments")
            .then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("durationMinutes", equalTo(30))
            .extract()
            .jsonPath()
            .getString("id");

        // 5b. Retrieve appointments by doctor's user ID and verify
        given()
            .header("Authorization", "Bearer " + adminToken)
            .queryParam("userId", doctorUserId)
            .when()
            .get("/api/appointments")
            .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1))
            .body("[0].patientId", equalTo(patientId))
            .body("[0].userId", equalTo(doctorUserId));

        // 5c. Retrieve appointments by patient ID and verify
        given()
            .header("Authorization", "Bearer " + adminToken)
            .queryParam("patientId", patientId)
            .when()
            .get("/api/appointments")
            .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1))
            .body("[0].patientId", equalTo(patientId))
            .body("[0].userId", equalTo(doctorUserId));

        // 6. Try to book a conflicting appointment for the same doctor (10:15 AM - 10:45 AM, overlapping with [10:00-10:30])
        String conflictingApptDateTime = "2026-06-15T10:15:00";
        String conflictingApptPayload = String.format("""
        {
            "patientId": "%s",
            "userId": "%s",
            "appointmentDateTime": "%s",
            "durationMinutes": 30,
            "appointmentFee": 100.00,
            "status": "SCHEDULED",
            "notes": "Conflicting doctor slot"
        }
        """, patient2Id, doctorUserId, conflictingApptDateTime);

        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType("application/json")
            .body(conflictingApptPayload)
            .when()
            .post("/api/appointments")
            .then()
            .statusCode(400)
            .body("message", containsString("already has an overlapping appointment"));

        // 7. Try to book a conflicting appointment for the same patient (10:10 AM - 10:40 AM, overlapping with [10:00-10:30])
        String patientConflictPayload = String.format("""
        {
            "patientId": "%s",
            "userId": "%s",
            "appointmentDateTime": "%s",
            "durationMinutes": 30,
            "appointmentFee": 100.00,
            "status": "SCHEDULED",
            "notes": "Conflicting patient slot"
        }
        """, patientId, doctorUserId, "2026-06-15T10:10:00");

        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType("application/json")
            .body(patientConflictPayload)
            .when()
            .post("/api/appointments")
            .then()
            .statusCode(400)
            .body("message", containsString("already has an overlapping appointment"));

        // 8. Book a non-conflicting consecutive appointment for the same doctor (10:30 AM - 11:00 AM, touching but not overlapping)
        String consecutiveApptDateTime = "2026-06-15T10:30:00";
        String consecutiveApptPayload = String.format("""
        {
            "patientId": "%s",
            "userId": "%s",
            "appointmentDateTime": "%s",
            "durationMinutes": 30,
            "appointmentFee": 100.00,
            "status": "SCHEDULED",
            "notes": "Consecutive slot"
        }
        """, patient2Id, doctorUserId, consecutiveApptDateTime);

        String apptId2 = given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType("application/json")
            .body(consecutiveApptPayload)
            .when()
            .post("/api/appointments")
            .then()
            .statusCode(200)
            .body("id", notNullValue())
            .extract()
            .jsonPath()
            .getString("id");

        // 8b. Verify filtering doctor appointments by date
        given()
            .header("Authorization", "Bearer " + adminToken)
            .queryParam("userId", doctorUserId)
            .queryParam("date", "2026-06-15")
            .when()
            .get("/api/appointments")
            .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("[0].appointmentDateTime", containsString("2026-06-15"))
            .body("[1].appointmentDateTime", containsString("2026-06-15"));

        given()
            .header("Authorization", "Bearer " + adminToken)
            .queryParam("userId", doctorUserId)
            .queryParam("date", "2026-06-16")
            .when()
            .get("/api/appointments")
            .then()
            .statusCode(200)
            .body("size()", equalTo(0));

        // 9. Clean up created appointments to prevent database bloat
        given()
            .header("Authorization", "Bearer " + adminToken)
            .when()
            .delete("/api/appointments/" + apptId1)
            .then()
            .statusCode(204);

        given()
            .header("Authorization", "Bearer " + adminToken)
            .when()
            .delete("/api/appointments/" + apptId2)
            .then()
            .statusCode(204);
    }
}
