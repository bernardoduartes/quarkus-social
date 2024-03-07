package br.shizuca.social.resource;

import br.shizuca.social.domain.model.User;
import br.shizuca.social.dto.CreateUserRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @POST
    @Transactional
    public Response create(CreateUserRequest userRequest) {
        User user = new User();
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());
        user.persist();
        return Response.ok(userRequest).build();
    }

    @GET
    public Response findAll() {
        return Response.ok(User.listAll()).build();
    }
}
