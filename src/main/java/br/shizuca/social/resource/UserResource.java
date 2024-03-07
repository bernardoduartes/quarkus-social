package br.shizuca.social.resource;

import br.shizuca.social.domain.model.User;
import br.shizuca.social.dto.CreateUserRequest;
import br.shizuca.social.exception.ResponseError;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Optional;
import java.util.Set;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final Validator validator;

    public UserResource(Validator validator) {
        this.validator = validator;
    }

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

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);

        if(!violations.isEmpty())
            return ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);

        User user = new User();
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());
        user.persist();

        return Response
                .status(Response.Status.CREATED.getStatusCode())
                .entity(user)
                .build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, CreateUserRequest userRequest){
        Optional<User> p = User.findByIdOptional(id);

        if(p.isPresent()) {
            p.get().setName(userRequest.getName());
            p.get().setAge(userRequest.getAge());
            User.persist(p.get());
        }else{
            throw new NotFoundException("User not foud!");
        }

        return Response.noContent().build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id){
        Optional<User> p = User.findByIdOptional(id);
        p.ifPresentOrElse(User::delete, () -> {
            throw new NotFoundException("User not foud!");
        });

        return Response.noContent().build();
    }
}