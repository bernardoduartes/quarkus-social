package br.shizuca.social.resource;

import br.shizuca.social.dto.CreateUserRequest;
import br.shizuca.social.exception.ResponseError;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

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

    @Test
    @DisplayName("should return error when JSON is not valid")
    public void should_return_error_when_JSON_is_nor_valid(){
        var user = new CreateUserRequest();
        var response =
                given()
                        .contentType(ContentType.JSON)
                        .body(user)
                        .when()
                        .post("/users")
                        .then()
                        .extract()
                        .response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> erros = response.jsonPath().getList("errors");
        assertNotNull(erros.get(0).get("message"));
        assertNotNull(erros.get(0).get("message"));
    }
}