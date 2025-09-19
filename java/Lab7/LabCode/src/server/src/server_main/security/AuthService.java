package server_main.security;


import server_main.db.UserDao;
import common_main.Request;

import java.util.Optional;

public class AuthService {
    private final UserDao users;

    public AuthService(UserDao users) { this.users = users; }

    public Optional<AuthUser> authenticate(Request r) {
        if (r.getLogin() == null || r.getPassword() == null) return Optional.empty();
        return users.auth(r.getLogin(), r.getPassword()).map(id -> new AuthUser(id, r.getLogin()));
    }

    public record AuthUser(long id, String login) {}
}
