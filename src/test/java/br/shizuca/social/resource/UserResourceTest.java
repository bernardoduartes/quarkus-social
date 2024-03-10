package br.shizuca.social.resource;

import br.shizuca.social.dto.CreateUserRequest;
import br.shizuca.social.exception.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @TestHTTPResource("/users")
    private URL USER_API_UTL;

    @Test
    @DisplayName("should create a user sucessfully")
    @Order(1)
    public void should_create_an_user_sucessfully(){

        var user = new CreateUserRequest();
        user.setAge(66);
        user.setName("User");

        var response =
                given()
                        .contentType(ContentType.JSON)
                        .body(user)
                        .when()
                        .post(USER_API_UTL)
                        .then()
                        .extract()
                        .response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.getBody().jsonPath().getString("id"));
    }

    @Test
    @DisplayName("should return error when JSON is not valid")
    @Order(2)
    public void should_return_error_when_JSON_is_nor_valid(){
        var user = new CreateUserRequest();
        var response =
                given()
                        .contentType(ContentType.JSON)
                        .body(user)
                        .when()
                        .post(USER_API_UTL)
                        .then()
                        .extract()
                        .response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> erros = response.jsonPath().getList("errors");
        assertNotNull(erros.get(0).get("message"));
        assertNotNull(erros.get(0).get("message"));
    }

    @Test
    @DisplayName("should list all users")
    @Order(3)
    public void should_list_all_users(){
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(USER_API_UTL)
                .then()
                .statusCode(200);
                //.body("size()", Matchers.is(46));
    }
}