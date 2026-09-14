package com.example.jobtracker;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    private final ApplicationRepository repository;

    public ApplicationController(ApplicationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ApplicationEntry> list(@RequestParam(required = false) String status) {
        return repository.findAll(status);
    }

    @GetMapping("/{id}")
    public ApplicationEntry get(@PathVariable long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
    }

    @PostMapping
    public ResponseEntity<ApplicationEntry> create(@Valid @RequestBody ApplicationRequest request) {
        ApplicationEntry created = repository.create(request);
        return ResponseEntity.created(URI.create("/api/applications/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public ApplicationEntry update(@PathVariable long id, @Valid @RequestBody ApplicationRequest request) {
        if (!repository.update(id, request)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found");
        }
        return get(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (!repository.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found");
        }
        return ResponseEntity.noContent().build();
    }
}
