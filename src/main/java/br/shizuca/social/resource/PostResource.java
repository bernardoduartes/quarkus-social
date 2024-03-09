package br.shizuca.social.resource;

import br.shizuca.social.domain.model.Follower;
import br.shizuca.social.domain.model.Post;
import br.shizuca.social.domain.model.User;
import br.shizuca.social.domain.repository.FollowerRepository;
import br.shizuca.social.domain.repository.PostRepository;
import br.shizuca.social.dto.CreatePostRequest;
import br.shizuca.social.dto.PostResponse;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {


    private PostRepository repository;
    private FollowerRepository followerRepository;

    @Inject
    public PostResource(PostRepository repository, FollowerRepository followerRepository) {
        this.repository = repository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request) {

        User user = User.findById(userId);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);
        post.setDateTime(LocalDateTime.now());
        repository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPosts(
            @PathParam("userId") Long userId,
            @HeaderParam("followerId") Long followerId) {

        if (Objects.isNull(followerId))
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("HeaderParam followerId is required")
                    .build();

        Optional<User> user = User.findByIdOptional(userId);
        if (!user.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Optional<User> fallowOptional = User.findByIdOptional(followerId);
        if (!fallowOptional.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Follow NOT_FOUND")
                    .build();
        }

        boolean isFallow = followerRepository.isFollower(fallowOptional.get(), user.get());

        if(!isFallow){
            return Response.status(Response.Status.FORBIDDEN).entity("You have no acess to the post").build();
        }

        var query = repository.find("user", Sort.by("dateTime", Sort.Direction.Descending),user.get());

        return Response.ok(
                query.list()
                        .stream()
                        .map(post -> PostResponse.fromEntity(post))
                        .collect(Collectors.toList())
        ).build();
    }
}
