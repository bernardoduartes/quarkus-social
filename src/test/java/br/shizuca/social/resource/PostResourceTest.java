package br.shizuca.social.resource;

import br.shizuca.social.domain.model.User;
import br.shizuca.social.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    private Long userId;

    @BeforeEach
    @Transactional
    public void setUP(){
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        User.persist(user);
        userId = user.getId();
    }

    @Test
    @DisplayName("should create a post for a user")
    public void should_create_a_post_for_a_user(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", getUserId())
                .when()
                .post()
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("should return 404 for post with an inexistent user")
    public void should_return_404_for_post_with_an_inexistent_user(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", inexistentUserId)
                .when()
                .post()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("should return 400 when followerId header is not present")
    public void should_return_400_when_followerId_header_is_not_present(){
        given()
                .pathParam("userId", userId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("HeaderParam followerId is required"));
    }

    @Test
    @DisplayName("should return 404 when user doesn't exist")
    public void should_return_404_when_user_doesnt_exist(){
        var inexistentUserId = 999;

        given()
                .pathParam("userId", inexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(404);
    }

    public Long getUserId() {
        return userId;
    }
}