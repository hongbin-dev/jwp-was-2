package webserver.controller;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.Map;

import http.HttpStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;
import webserver.persist.Users;

public class LoginController implements Controller {

    private final Users users;

    public LoginController(Users users) {
        this.users = users;
    }

    @Override
    public HttpResponse run(HttpRequest request) {
        var body = request.getBody();
        var params = Arrays.stream(body.split("&"))
            .map(it -> it.split("="))
            .map(it -> Map.entry(it[0], it[1]))
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        var userId = params.get("userId");

        var isLogined = users.findBy(userId)
            .filter(it -> it.canLogin(params.get("password")))
            .isPresent();

        return new HttpResponse(HttpStatus.OK, Map.of("Set-Cookie", String.format("logined=%s; Path=/", isLogined)));
    }
}
