package br.shizuca.social.resource;

import br.shizuca.social.dto.CreateUserRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserResourceTest {

    @Test
    @DisplayName("should create an user sucessfully")
    public void should_create_an_user_sucessfully(){

        var user = new CreateUserRequest();
        user.setAge(66);
        user.setName("User");

        var response =
                given()
                        .contentType(ContentType.JSON)
                        .body(user)
                        .when()
                        .post("/users")
                        .then()
                        .extract()
                        .response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.getBody().jsonPath().getString("id"));
    }
}