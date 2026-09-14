package com.example.jobtracker;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:tracker-test;DB_CLOSE_DELAY=-1")
class ApplicationRepositoryTest {
    @Autowired ApplicationRepository repository;
    @Autowired JdbcTemplate jdbc;

    @Test
    void persistsAndChangesApplicationsWithSql() {
        jdbc.update("DELETE FROM applications");
        var applied = new ApplicationRequest("Example Co", "Developer", LocalDate.parse("2026-09-10"),
                "https://example.com/job", "Applied", "Follow up");
        var created = repository.create(applied);

        assertThat(created.id()).isPositive();
        assertThat(repository.findById(created.id())).contains(created);
        assertThat(repository.findAll("Applied")).contains(created);
        assertThat(repository.findAll("Interview")).isEmpty();

        var newer = repository.create(new ApplicationRequest("O'Connor Labs", "Tester",
                LocalDate.parse("2026-09-12"), "", "Applied", ""));
        assertThat(repository.findAll(null)).extracting(ApplicationEntry::id)
                .containsExactly(newer.id(), created.id());

        var interview = new ApplicationRequest("Example Co", "Developer", LocalDate.parse("2026-09-10"),
                "https://example.com/job", "Interview", "Tuesday at 10");
        assertThat(repository.update(created.id(), interview)).isTrue();
        assertThat(repository.findById(created.id()).orElseThrow().status()).isEqualTo("Interview");
        assertThat(repository.findAll("Applied")).containsExactly(newer);
        assertThat(repository.delete(created.id())).isTrue();
        assertThat(repository.delete(newer.id())).isTrue();
        assertThat(repository.findById(created.id())).isEmpty();
        assertThat(repository.update(created.id(), interview)).isFalse();
    }
}
