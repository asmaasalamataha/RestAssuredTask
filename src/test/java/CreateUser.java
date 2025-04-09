import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateUser {
    private static String baseUrl = "https://reqres.in/api";
    private static String userId;
    private static String validUserId;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = baseUrl;
        
        Response response = given()
                .when()
                .get("/users?page=1")
                .then()
                .statusCode(200)
                .extract().response();

        validUserId = response.jsonPath().getString("data[0].id"); 
        System.out.println("Valid User ID from List: " + validUserId);
    }

    @Test(priority = 1)
    public void createUser() {
        String requestBody = "{\"name\": \"John Doe\", \"job\": \"QA Engineer\"}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("name", equalTo("John Doe"))
                .body("job", equalTo("QA Engineer"))
                .extract().response();

        userId = response.jsonPath().getString("id");
        System.out.println("Created User ID: " + userId); //
        Assert.assertNotNull(userId, "User ID should not be null");
    }

    @Test(priority = 2, dependsOnMethods = "createUser")
    public void getUser() {
        Assert.assertNotNull(validUserId, "Valid User ID should not be null before retrieving user.");

        given()
                .pathParam("id", validUserId)  //
                .when()
                .get("/users/{id}")
                .then()
                .statusCode(200)
                .body("data.id", equalTo(Integer.parseInt(validUserId)))
        .extract().response();


    }

    @Test(priority = 3, dependsOnMethods = "createUser")
    public void updateUser() {
        String updatedBody = "{\"name\": \"Doe Updated\", \"job\": \"QA Engineer\"}";

        Response response=
                given()
                .contentType(ContentType.JSON)
                .body(updatedBody)
                .pathParam("id", validUserId)  
                .when()
                .put("/users/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("Doe Updated"))
                .body("job", equalTo("QA Engineer"))
                .extract().response();


        // ✅ Assertions for updated values
        Assert.assertEquals(response.jsonPath().getString("name"), "Doe Updated", "Name did not update correctly");
        Assert.assertEquals(response.jsonPath().getString("job"), "QA Engineer", "Job did not update correctly");

        System.out.println("User updated successfully: " + response.asString());
    }

    @AfterClass
    public void getFinalUserData() {
        Assert.assertNotNull(validUserId, "User ID should not be null after all tests.");

        Response response = given()
                .pathParam("id", validUserId)
                .when()
                .get("/users/{id}")
                .then()
                .statusCode(200)
                .extract().response();

        System.out.println("Final user data after updates: " + response.asString());
    }
}






