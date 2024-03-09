package br.shizuca.social.resource;

import br.shizuca.social.domain.model.Follower;
import br.shizuca.social.domain.model.User;
import br.shizuca.social.domain.repository.FollowerRepository;
import br.shizuca.social.dto.FollowerRequest;
import br.shizuca.social.dto.FollowerResponse;
import br.shizuca.social.dto.FollowersPerUserResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Optional;
import java.util.stream.Collectors;

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

        if(userId.equals(request.getFollowerId())){
            return Response.status(Response.Status.CONFLICT)
                    .entity("You cant fallow youself")
                    .build();
        }

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

    @GET
    public Response listFollowers(@PathParam("userId") Long userId){

        var user = User.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var list = repository.findByUser(userId);
        FollowersPerUserResponse responseObject = new FollowersPerUserResponse();
        responseObject.setFollowersCount(list.size());

        var followerList = list.stream()
                .map(FollowerResponse::new)
                .collect(Collectors.toList());

        responseObject.setContent(followerList);
        return Response.ok(responseObject).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(
            @PathParam("userId") Long userId,
            @QueryParam("followerId")  Long followerId ){

        var user = User.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        repository.deleteByFollowerAndUser(followerId, userId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
