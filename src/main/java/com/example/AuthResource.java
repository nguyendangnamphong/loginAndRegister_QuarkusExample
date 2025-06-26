package com.example;

import org.mindrot.jbcrypt.BCrypt;
import io.quarkus.qute.Template;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.jwt.Claims;
import java.util.List;
import org.jboss.logging.Logger;

@Path("/api")
public class AuthResource {

    private static final Logger LOG = Logger.getLogger(AuthResource.class);

    @Inject
    Template login;

    @Inject
    Template register;

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    public String getLogin() {
        return login.instance().render();
    }

    @GET
    @Path("/register")
    @Produces(MediaType.TEXT_HTML)
    public String getRegister() {
        return register.instance().render();
    }

    @POST
    @Path("/register")
    @Transactional
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("name") String name,
            @FormParam("age") Integer age) {
        try {
            LOG.info("Attempting to register user: " + username);
            if (username == null || password == null || name == null || age == null) {
                LOG.error("Missing required fields");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"All fields are required\"}")
                        .build();
            }
            User existingUser = User.findByUsername(username);
            LOG.info("Existing user check: " + (existingUser != null ? existingUser.username : "null"));
            if (existingUser != null) {
                LOG.warn("Username already exists: " + username);
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\": \"Username already exists\"}")
                        .build();
            }
            User user = new User();
            user.username = username;
            user.password = BCrypt.hashpw(password, BCrypt.gensalt());
            user.name = name;
            user.age = age;
            user.persist();
            LOG.info("User registered successfully: " + username);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Registration successful\"}")
                    .build();
        } catch (Exception e) {
            LOG.error("Registration failed", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
            @FormParam("username") String username,
            @FormParam("password") String password) {
        try {
            if (username == null || password == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Username and password are required\"}")
                        .build();
            }
            User user = User.findByUsername(username);
            if (user != null && BCrypt.checkpw(password, user.password)) {
                LOG.info("Generating JWT for user: " + username);
                String token = Jwt.issuer("quarkus-sample")
                        .subject(username)
                        .upn(username)
                        .groups("user")
                        .claim(Claims.full_name, user.name)
                        .claim("age", user.age)
                        .sign();
                LOG.info("JWT generated successfully");
                return Response.ok()
                        .entity("{\"token\": \"" + token + "\"}")
                        .build();
            }
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Invalid username or password\"}")
                    .build();
        } catch (Exception e) {
            LOG.error("Login failed", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"SRJWT05009: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/login-json")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginJson(LoginRequest request) {
        try {
            // Debug bước 1: Kiểm tra request object
            LOG.info("=== LOGIN-JSON DEBUG START ===");
            LOG.info("Request object: " + (request != null ? request.toString() : "NULL"));

            if (request == null) {
                LOG.error("Request object is null - JSON parsing failed");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Request object is null - JSON parsing failed\"}")
                        .build();
            }

            LOG.info("Username: " + request.username);
            LOG.info("Password: " + (request.password != null ? "***PROVIDED***" : "NULL"));

            if (request.username == null || request.password == null) {
                LOG.error("Username or password is null");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Username and password are required\"}")
                        .build();
            }

            // Debug bước 2: Tìm user trong database
            LOG.info("Looking for user: " + request.username);
            User user = User.findByUsername(request.username);
            LOG.info("User found: " + (user != null ? "YES" : "NO"));

            if (user != null) {
                LOG.info("User details - name: " + user.name + ", age: " + user.age);

                // Debug bước 3: Kiểm tra password
                boolean passwordMatch = BCrypt.checkpw(request.password, user.password);
                LOG.info("Password match: " + passwordMatch);

                if (passwordMatch) {
                    LOG.info("=== GENERATING JWT ===");

                    // Debug JWT generation
                    try {
                        String token = Jwt.issuer("quarkus-sample")
                                .subject(request.username)
                                .upn(request.username)
                                .groups("user")
                                .claim(Claims.full_name, user.name)
                                .claim("age", user.age)
                                .sign();

                        LOG.info("JWT generated successfully, length: " + token.length());
                        return Response.ok()
                                .entity("{\"token\": \"" + token + "\"}")
                                .build();

                    } catch (Exception jwtException) {
                        LOG.error("JWT generation failed", jwtException);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                .entity("{\"error\": \"JWT generation failed: " + jwtException.getMessage() + "\"}")
                                .build();
                    }
                }
            }

            LOG.warn("Authentication failed for user: " + request.username);
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Invalid username or password\"}")
                    .build();

        } catch (Exception e) {
            LOG.error("Unexpected error in loginJson", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Unexpected error: " + e.getClass().getSimpleName() + " - " + e.getMessage() + "\"}")
                    .build();
        } finally {
            LOG.info("=== LOGIN-JSON DEBUG END ===");
        }
    }

    public static class LoginRequest {
        @JsonProperty("username")
        public String username;

        @JsonProperty("password")
        public String password;

        @Override
        public String toString() {
            return "LoginRequest{username=" + username + ", password=****}";
        }
    }


    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public List<User> getUsers() {
        return User.listAll();
    }
}