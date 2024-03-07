package br.shizuca.social.resource;

import br.shizuca.social.domain.model.User;
import br.shizuca.social.dto.CreateUserRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @GET
    public Response findAll() {
        return Response.ok(User.listAll()).build();
    }

    @GET
    @Path("{id}")
    public Response findById(@PathParam("id") Long id) {
        Optional<User> p = User.findByIdOptional(id);

        if(p.isPresent())
            return Response.ok(p.get()).build();

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Transactional
    public Response create(CreateUserRequest userRequest) {
        User user = new User();
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());
        user.persist();
        return Response.ok(userRequest).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public void update(@PathParam("id") Long id, CreateUserRequest userRequest){
        Optional<User> p = User.findByIdOptional(id);

        if(p.isPresent()) {
            p.get().setName(userRequest.getName());
            p.get().setAge(userRequest.getAge());
            User.persist(p.get());
        }else{
            throw new NotFoundException("User not foud!");
        }
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public void create(@PathParam("id") Long id){
        Optional<User> p = User.findByIdOptional(id);
        p.ifPresentOrElse(User::delete, () -> {
            throw new NotFoundException("User not foud!");
        });
    }
}