package com.example;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.mindrot.jbcrypt.BCrypt;

@Path("/")
public class WebResource {

    @Inject
    Template index;

    @Inject
    AuthResource auth;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getIndex() {
        return index.data("registerMessage", null, "loginMessage", null);
    }

    @POST
    @Path("/register")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance handleRegister(
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("name") String name,
            @FormParam("age") Integer age) {
        User user = new User();
        user.username = username;
        user.password = password;
        user.name = name;
        user.age = age;

        Response response = auth.register(username, password, name, age);
        return index.data("registerMessage", response.getEntity(), "registerError", response.getStatus() != 201);
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance handleLogin(
            @FormParam("username") String username,
            @FormParam("password") String password) {
        Response response = auth.login(username, password);
        return index.data("loginMessage", response.getEntity(), "loginError", response.getStatus() != 200);
    }
}