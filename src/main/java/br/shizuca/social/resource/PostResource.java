package br.shizuca.social.resource;

import br.shizuca.social.domain.model.Post;
import br.shizuca.social.domain.model.User;
import br.shizuca.social.domain.repository.PostRepository;
import br.shizuca.social.dto.CreatePostRequest;
import br.shizuca.social.dto.PostResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {


    private PostRepository repository;

    @Inject
    public PostResource(PostRepository repository) {
        this.repository = repository;
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
    public Response listPosts(@PathParam("userId") Long userId) {
        Optional<User> user = User.findByIdOptional(userId);
        if (!user.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var query = repository.find("user", user.get());

        return Response.ok(
                query.list()
                        .stream()
                        .map(post -> PostResponse.fromEntity(post))
                        .collect(Collectors.toList())
        ).build();
    }
}
