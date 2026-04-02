package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.demo.repository.UserRepository;
import com.example.demo.model.User;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository repo;
    public DataInitializer(UserRepository repo) { this.repo = repo; }

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            repo.save(new User("alice", "password", "Alice Dupont", "alice@example.com"));
            repo.save(new User("bob", "password", "Bob Martin", "bob@example.com"));
            repo.save(new User("carol", "password", "Carol Smith", "carol@example.com"));
        }
    }
}