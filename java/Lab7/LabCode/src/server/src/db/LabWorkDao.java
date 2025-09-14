package src.server.src.db;


import src.common.src.LabWork;
import src.common.src.net.Serde;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LabWorkDao {
    private final Db db;

    public LabWorkDao(Db db) { this.db = db; }

    public List<LabWork> loadAll() {
        String sql = "select id, owner_login, payload from labwork order by id";
        List<LabWork> out = new ArrayList<>();
        try (Connection c = db.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long id = rs.getLong(1);
                String owner = rs.getString(2);
                byte[] payload = rs.getBytes(3);
                LabWork lw = Serde.fromBytes(payload);
                lw.setId(id);
                lw.setOwnerLogin(owner);
                out.add(lw);
            }
        } catch (Exception ignored) {}
        return out;
    }

    public Optional<Long> insert(LabWork lw, long ownerId, String ownerLogin) {
        String sql = "insert into labwork(owner_id, owner_login, payload) values (?, ?, ?) returning id";
        try (Connection c = db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            byte[] blob = Serde.toBytes(lw);
            ps.setLong(1, ownerId);
            ps.setString(2, ownerLogin);
            ps.setBytes(3, blob);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { return Optional.of(rs.getLong(1)); }
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    public boolean update(LabWork lw, long ownerId) {
        String sql = "update labwork set payload = ? where id = ? and owner_id = ?";
        try (Connection c = db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBytes(1, Serde.toBytes(lw));
            ps.setLong(2, lw.getId());
            ps.setLong(3, ownerId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteById(long id, long ownerId) {
        String sql = "delete from labwork where id = ? and owner_id = ?";
        try (Connection c = db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, ownerId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            return false;
        }
    }
}
