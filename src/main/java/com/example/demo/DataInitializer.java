package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository repo;
    public DataInitializer(UserRepository repo) { this.repo = repo; }

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            repo.save(new User("Alice Dupont", "alice@example.com"));
            repo.save(new User("Bob Martin", "bob@example.com"));
            repo.save(new User("Carol Smith", "carol@example.com"));
        }
    }
}