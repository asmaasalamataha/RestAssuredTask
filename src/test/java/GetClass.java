import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GetClass {

    private static final String BASE_URL = "https://reqres.in/api";
    String userId;
        @Test
        public void testCreateUser() {
            String requestBody = "{ \"name\": \"John\", \"job\": \"QA Engineer\" }";
            String userId;
            Response response = given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .post("/users")
                    .then()
                    .statusCode(201)
                    .body("name", equalTo("John"))
                    .body("job", equalTo("QA Engineer"))
                    .extract().response();

            System.out.println("Response: " + response.asString());

            userId = response.jsonPath().getString("id");
            Assert.assertNotNull(userId, "User ID should not be null");
        }

    @Test(priority = 2, dependsOnMethods = "testCreateUser")
    public void getUser() {
        Assert.assertNotNull(userId, "User ID should not be null before retrieving user.");

        Response response = given()
                .pathParam("id", userId)
                .when()
                .get("/users/{id}")
                .then()
                .statusCode(200)
                .extract().response();

        System.out.println("Response: " + response.asString());
    }

}





