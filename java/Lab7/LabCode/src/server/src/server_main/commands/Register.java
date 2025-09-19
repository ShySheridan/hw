package server_main.commands;


import server_main.db.UserDao;
import common_main.Request;
import common_main.Response;

public class Register implements Command {
    private final UserDao users;

    public Register(UserDao users) {
        this.users = users;
    }

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getDescription() {
        return "регистрация пользователя (исп: register <ignored>)";
    }

    @Override
    public Response execute(Request request) {
        if (request.getLogin() == null || request.getPassword() == null)
            return Response.fail("Укажите логин и пароль (введите их при запуске клиента).");
        boolean ok = users.register(request.getLogin(), request.getPassword());
        return ok ? Response.ok("Пользователь зарегистрирован.")
                : Response.fail("Такой логин уже существует.");
    }
}
