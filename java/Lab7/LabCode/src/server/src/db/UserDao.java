package src.server.src.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import src.server.src.security.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class UserDao {
    private static final Logger log = LogManager.getLogger(UserDao.class);
    private final Db db;

    public UserDao(Db db) { this.db = db; }

    public boolean register(String login, String rawPassword) {
        String sql = "insert into app_user(login, password_hash) values (?, ?)";
        try (Connection c = db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, PasswordUtil.sha224(rawPassword));
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            log.error(e);
            return false;
        }
    }

    public Optional<Long> auth(String login, String rawPassword) {
        String sql = "select id, password_hash from app_user where login = ?";
        try (Connection c = db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                long id = rs.getLong(1);
                String hash = rs.getString(2);
                String check = PasswordUtil.sha224(rawPassword);
                return hash.equals(check) ? Optional.of(id) : Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
