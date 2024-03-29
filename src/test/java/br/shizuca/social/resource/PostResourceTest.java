package br.shizuca.social.resource;

import br.shizuca.social.domain.model.Follower;
import br.shizuca.social.domain.model.Post;
import br.shizuca.social.domain.model.User;
import br.shizuca.social.domain.repository.FollowerRepository;
import br.shizuca.social.domain.repository.PostRepository;
import br.shizuca.social.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    FollowerRepository followerRepository;
    @Inject
    PostRepository postRepository;

    private Long userId;
    private Long userFollowerId;
    private Long userNotFollowerId;

    @BeforeEach
    @Transactional
    public void setUP(){
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        User.persist(user);
        userId = user.getId();

        //criada a postagem para o usuario
        Post post = new Post();
        post.setText("Hello");
        post.setUser(user);
        postRepository.persist(post);

        var userFollower = new User();
        userFollower.setAge(31);
        userFollower.setName("Pedro");
        User.persist(userFollower);
        userFollowerId = userFollower.getId();

        var userNotFollower = new User();
        userNotFollower.setAge(52);
        userNotFollower.setName("João");
        User.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);

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
                .headers("followerId", userFollowerId)
                .when()
                .get()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("should return 404 when follower doesn't exist")
    public void should_return_404_when_follower_doesnt_exist(){
        var userFollowerId = 999;

        given()
                .pathParam("userId", userId)
                .headers("followerId", userFollowerId)
                .when()
                .get()
                .then()
                .statusCode(404)
                .body(Matchers.is("Follow NOT_FOUND"));
    }

    @Test
    @DisplayName("should return 403 when follower isn't a user follower")
    public void should_return_403_when_user_isnt_a_user_follower(){
        given()
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
                .when()
                .get()
                .then()
                .statusCode(403)
                .body(Matchers.is("You have no acess to the post"));
    }

    @Test
    @DisplayName("should list posts")
    public void should_list_posts(){
        given()
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }

    public Long getUserId() {
        return userId;
    }

    public Long getUserFollowerId() {
        return userFollowerId;
    }

    public Long getUserNotFollowerId() {
        return userNotFollowerId;
    }
}