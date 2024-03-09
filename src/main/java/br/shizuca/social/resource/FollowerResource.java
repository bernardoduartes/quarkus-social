package br.shizuca.social.resource;

import br.shizuca.social.domain.model.Follower;
import br.shizuca.social.domain.model.User;
import br.shizuca.social.domain.repository.FollowerRepository;
import br.shizuca.social.dto.FollowerRequest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private FollowerRepository repository;

    @Inject
    public FollowerResource(FollowerRepository repository){
        this.repository = repository;
    }

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest request){

        Optional<User> userOptional = User.findByIdOptional(userId);
        if (!userOptional.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Optional<User> followerOptional = User.findByIdOptional(request.getFollowerId());
        if (!followerOptional.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        boolean isFollower = repository.isFollower(followerOptional.get(), userOptional.get());

        if(!isFollower) {
            var entity = new Follower();
            entity.setUser(userOptional.get());
            entity.setFollower(followerOptional.get());
            repository.persist(entity);
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
