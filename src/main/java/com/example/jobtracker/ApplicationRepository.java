package com.example.jobtracker;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationRepository {
    private static final String COLUMNS = "id, company, role, date_applied, url, status, notes";
    private static final RowMapper<ApplicationEntry> ROW_MAPPER = (row, number) -> new ApplicationEntry(
            row.getLong("id"),
            row.getString("company"),
            row.getString("role"),
            row.getObject("date_applied", LocalDate.class),
            row.getString("url"),
            row.getString("status"),
            row.getString("notes")
    );

    private final JdbcTemplate jdbc;

    public ApplicationRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ApplicationEntry> findAll(String status) {
        if (status == null) {
            return jdbc.query("SELECT " + COLUMNS + " FROM applications ORDER BY date_applied DESC, id DESC", ROW_MAPPER);
        }
        return jdbc.query("SELECT " + COLUMNS + " FROM applications WHERE status = ? ORDER BY date_applied DESC, id DESC",
                ROW_MAPPER, status);
    }

    public Optional<ApplicationEntry> findById(long id) {
        return jdbc.query("SELECT " + COLUMNS + " FROM applications WHERE id = ?", ROW_MAPPER, id)
                .stream().findFirst();
    }

    public ApplicationEntry create(ApplicationRequest request) {
        var keys = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO applications (company, role, date_applied, url, status, notes) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, request.company());
            statement.setString(2, request.role());
            statement.setObject(3, request.dateApplied());
            statement.setString(4, request.url());
            statement.setString(5, request.status());
            statement.setString(6, request.notes());
            return statement;
        }, keys);
        return findById(keys.getKey().longValue()).orElseThrow();
    }

    public boolean update(long id, ApplicationRequest request) {
        return jdbc.update("UPDATE applications SET company = ?, role = ?, date_applied = ?, url = ?, status = ?, notes = ? WHERE id = ?",
                request.company(), request.role(), request.dateApplied(), request.url(), request.status(), request.notes(), id) > 0;
    }

    public boolean delete(long id) {
        return jdbc.update("DELETE FROM applications WHERE id = ?", id) > 0;
    }
}
